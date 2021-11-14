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

describe('CatalogBrand e2e test', () => {
  const catalogBrandPageUrl = '/catalog-brand';
  const catalogBrandPageUrlPattern = new RegExp('/catalog-brand(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const catalogBrandSample = { brand: 'Macao' };

  let catalogBrand: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/catalog-brands').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/catalog-brands+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/catalog-brands').as('postEntityRequest');
    cy.intercept('DELETE', '/api/catalog-brands/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (catalogBrand) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/catalog-brands/${catalogBrand.id}`,
      }).then(() => {
        catalogBrand = undefined;
      });
    }
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('CatalogBrands menu should load CatalogBrands page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('catalog-brand');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CatalogBrand').should('exist');
    cy.url().should('match', catalogBrandPageUrlPattern);
  });

  describe('CatalogBrand page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(catalogBrandPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CatalogBrand page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/catalog-brand/new$'));
        cy.getEntityCreateUpdateHeading('CatalogBrand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogBrandPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/catalog-brands',
          body: catalogBrandSample,
        }).then(({ body }) => {
          catalogBrand = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/catalog-brands+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [catalogBrand],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(catalogBrandPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CatalogBrand page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('catalogBrand');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogBrandPageUrlPattern);
      });

      it('edit button click should load edit CatalogBrand page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CatalogBrand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogBrandPageUrlPattern);
      });

      it('last delete button click should delete instance of CatalogBrand', () => {
        cy.intercept('GET', '/api/catalog-brands/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('catalogBrand').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogBrandPageUrlPattern);

        catalogBrand = undefined;
      });
    });
  });

  describe('new CatalogBrand page', () => {
    beforeEach(() => {
      cy.visit(`${catalogBrandPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('CatalogBrand');
    });

    it('should create an instance of CatalogBrand', () => {
      cy.get(`[data-cy="brand"]`).type('SMS').should('have.value', 'SMS');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        catalogBrand = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', catalogBrandPageUrlPattern);
    });
  });
});
