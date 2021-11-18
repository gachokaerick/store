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

describe('CatalogType e2e test', () => {
  const catalogTypePageUrl = '/catalog-type';
  const catalogTypePageUrlPattern = new RegExp('/catalog-type(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const catalogTypeSample = { type: 'Baby synthesizing' };

  let catalogType: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/catalog/api/catalog-types').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/catalog/api/catalog-types+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/catalog/api/catalog-types').as('postEntityRequest');
    cy.intercept('DELETE', '/services/catalog/api/catalog-types/*').as('deleteEntityRequest');
  });

  afterEach(() => {
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

  it('CatalogTypes menu should load CatalogTypes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('catalog-type');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CatalogType').should('exist');
    cy.url().should('match', catalogTypePageUrlPattern);
  });

  describe('CatalogType page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(catalogTypePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CatalogType page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/catalog-type/new$'));
        cy.getEntityCreateUpdateHeading('CatalogType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogTypePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/catalog/api/catalog-types',
          body: catalogTypeSample,
        }).then(({ body }) => {
          catalogType = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/catalog/api/catalog-types+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [catalogType],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(catalogTypePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CatalogType page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('catalogType');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogTypePageUrlPattern);
      });

      it('edit button click should load edit CatalogType page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CatalogType');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogTypePageUrlPattern);
      });

      it('last delete button click should delete instance of CatalogType', () => {
        cy.intercept('GET', '/services/catalog/api/catalog-types/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('catalogType').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', catalogTypePageUrlPattern);

        catalogType = undefined;
      });
    });
  });

  describe('new CatalogType page', () => {
    beforeEach(() => {
      cy.visit(`${catalogTypePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('CatalogType');
    });

    it('should create an instance of CatalogType', () => {
      cy.get(`[data-cy="type"]`).type('proactive distributed').should('have.value', 'proactive distributed');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        catalogType = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', catalogTypePageUrlPattern);
    });
  });
});
