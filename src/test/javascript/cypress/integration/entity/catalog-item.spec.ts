import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('CatalogItem e2e test', () => {
  const catalogItemPageUrl = '/catalog-item';
  const catalogItemPageUrlPattern = new RegExp('/catalog-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const catalogItemSample = { name: 'PCI ADP', price: 77989, availableStock: 66011, restockThreshold: 5988, maxStockThreshold: 40131 };

  let catalogItem: any;
  let catalogBrand: any;
  let catalogType: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/catalog/api/catalog-items').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/catalog/api/catalog-brands',
      body: { brand: 'Bike quantifying' },
    }).then(({ body }) => {
      catalogBrand = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/catalog/api/catalog-types',
      body: { type: 'deposit 24/7 Chips' },
    }).then(({ body }) => {
      catalogType = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/catalog/api/catalog-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/catalog/api/catalog-items').as('postEntityRequest');
    cy.intercept('DELETE', '/services/catalog/api/catalog-items/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/services/catalog/api/catalog-brands', {
      statusCode: 200,
      body: [catalogBrand],
    });

    cy.intercept('GET', '/services/catalog/api/catalog-types', {
      statusCode: 200,
      body: [catalogType],
    });
  });

  afterEach(() => {
    if (catalogItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/catalog/api/catalog-items/${catalogItem.id}`,
      }).then(() => {
        catalogItem = undefined;
      });
    }
  });

  afterEach(() => {
    if (catalogBrand) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/catalog/api/catalog-brands/${catalogBrand.id}`,
      }).then(() => {
        catalogBrand = undefined;
      });
    }
    if (catalogType) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/catalog/api/catalog-types/${catalogType.id}`,
      }).then(() => {
        catalogType = undefined;
      });
    }
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('CatalogItems menu should load CatalogItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('catalog-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CatalogItem').should('exist');
    cy.url().should('match', catalogItemPageUrlPattern);
  });

  describe('CatalogItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(catalogItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CatalogItem page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/catalog-item/new$'));
        cy.getEntityCreateUpdateHeading('CatalogItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/catalog/api/catalog-items',

          body: {
            ...catalogItemSample,
            catalogBrand: catalogBrand,
            catalogType: catalogType,
          },
        }).then(({ body }) => {
          catalogItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/catalog/api/catalog-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [catalogItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(catalogItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CatalogItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('catalogItem');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogItemPageUrlPattern);
      });

      it('edit button click should load edit CatalogItem page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CatalogItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogItemPageUrlPattern);
      });

      it('last delete button click should delete instance of CatalogItem', () => {
        cy.intercept('GET', '/services/catalog/api/catalog-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('catalogItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogItemPageUrlPattern);

        catalogItem = undefined;
      });
    });
  });

  describe('new CatalogItem page', () => {
    beforeEach(() => {
      cy.visit(`${catalogItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('CatalogItem');
    });

    it('should create an instance of CatalogItem', () => {
      cy.get(`[data-cy="name"]`).type('Korea Agent').should('have.value', 'Korea Agent');

      cy.get(`[data-cy="description"]`).type('program Account card').should('have.value', 'program Account card');

      cy.get(`[data-cy="price"]`).type('35238').should('have.value', '35238');

      cy.get(`[data-cy="pictureFileName"]`).type('Namibia').should('have.value', 'Namibia');

      cy.get(`[data-cy="pictureUrl"]`).type('e-commerce content').should('have.value', 'e-commerce content');

      cy.get(`[data-cy="availableStock"]`).type('48673').should('have.value', '48673');

      cy.get(`[data-cy="restockThreshold"]`).type('74944').should('have.value', '74944');

      cy.get(`[data-cy="maxStockThreshold"]`).type('15553').should('have.value', '15553');

      cy.get(`[data-cy="onReorder"]`).should('not.be.checked');
      cy.get(`[data-cy="onReorder"]`).click().should('be.checked');

      cy.get(`[data-cy="catalogBrand"]`).select(1);
      cy.get(`[data-cy="catalogType"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        catalogItem = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', catalogItemPageUrlPattern);
    });
  });
});
