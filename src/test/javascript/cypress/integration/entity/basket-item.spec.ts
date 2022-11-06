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

describe('BasketItem e2e test', () => {
  const basketItemPageUrl = '/basket-item';
  const basketItemPageUrlPattern = new RegExp('/basket-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const basketItemSample = {
    productId: 14583,
    productName: 'Card Heights',
    unitPrice: 60068,
    oldUnitPrice: 35571,
    quantity: 59563,
    pictureUrl: 'synthesizing Enterprise-wide turquoise',
    userLogin: 'compress Outdoors Account',
  };

  let basketItem: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/basket/api/basket-items').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/basket/api/basket-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/basket/api/basket-items').as('postEntityRequest');
    cy.intercept('DELETE', '/services/basket/api/basket-items/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (basketItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/basket/api/basket-items/${basketItem.id}`,
      }).then(() => {
        basketItem = undefined;
      });
    }
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('BasketItems menu should load BasketItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('basket-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BasketItem').should('exist');
    cy.url().should('match', basketItemPageUrlPattern);
  });

  describe('BasketItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(basketItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BasketItem page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/basket-item/new$'));
        cy.getEntityCreateUpdateHeading('BasketItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/basket/api/basket-items',
          body: basketItemSample,
        }).then(({ body }) => {
          basketItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/basket/api/basket-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [basketItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(basketItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BasketItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('basketItem');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketItemPageUrlPattern);
      });

      it('edit button click should load edit BasketItem page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BasketItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketItemPageUrlPattern);
      });

      it('last delete button click should delete instance of BasketItem', () => {
        cy.intercept('GET', '/services/basket/api/basket-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('basketItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketItemPageUrlPattern);

        basketItem = undefined;
      });
    });
  });

  describe('new BasketItem page', () => {
    beforeEach(() => {
      cy.visit(`${basketItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('BasketItem');
    });

    it('should create an instance of BasketItem', () => {
      cy.get(`[data-cy="productId"]`).type('6585').should('have.value', '6585');

      cy.get(`[data-cy="productName"]`).type('synthesize payment').should('have.value', 'synthesize payment');

      cy.get(`[data-cy="unitPrice"]`).type('76453').should('have.value', '76453');

      cy.get(`[data-cy="oldUnitPrice"]`).type('65729').should('have.value', '65729');

      cy.get(`[data-cy="quantity"]`).type('73242').should('have.value', '73242');

      cy.get(`[data-cy="pictureUrl"]`).type('Peso Granite methodologies').should('have.value', 'Peso Granite methodologies');

      cy.get(`[data-cy="userLogin"]`).type('quantifying').should('have.value', 'quantifying');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        basketItem = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', basketItemPageUrlPattern);
    });
  });
});
