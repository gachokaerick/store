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

describe('Authorization e2e test', () => {
  const authorizationPageUrl = '/authorization';
  const authorizationPageUrlPattern = new RegExp('/authorization(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const authorizationSample = {
    status: 'Licensed',
    authId: 'Consultant neural payment',
    currencyCode: 'PGK',
    amount: 98279,
    expirationTime: '2022-03-17T20:03:26.092Z',
    paymentProvider: 'PAYPAL',
  };

  let authorization: any;
  //let order: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/orders/api/authorizations').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/services/orders/api/orders',
      body: {"orderDate":"2021-11-14T03:33:48.217Z","orderStatus":"DRAFT","description":"RAM Soft complexity"},
    }).then(({ body }) => {
      order = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/services/orders/api/authorizations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/orders/api/authorizations').as('postEntityRequest');
    cy.intercept('DELETE', '/services/orders/api/authorizations/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/services/orders/api/orders', {
      statusCode: 200,
      body: [order],
    });

  });
   */

  afterEach(() => {
    if (authorization) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/orders/api/authorizations/${authorization.id}`,
      }).then(() => {
        authorization = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (order) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/orders/api/orders/${order.id}`,
      }).then(() => {
        order = undefined;
      });
    }
  });
   */

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('Authorizations menu should load Authorizations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('authorization');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Authorization').should('exist');
    cy.url().should('match', authorizationPageUrlPattern);
  });

  describe('Authorization page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(authorizationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Authorization page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/authorization/new$'));
        cy.getEntityCreateUpdateHeading('Authorization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', authorizationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/orders/api/authorizations',
  
          body: {
            ...authorizationSample,
            order: order,
          },
        }).then(({ body }) => {
          authorization = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/orders/api/authorizations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [authorization],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(authorizationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(authorizationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Authorization page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('authorization');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', authorizationPageUrlPattern);
      });

      it('edit button click should load edit Authorization page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Authorization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', authorizationPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Authorization', () => {
        cy.intercept('GET', '/services/orders/api/authorizations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('authorization').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', authorizationPageUrlPattern);

        authorization = undefined;
      });
    });
  });

  describe('new Authorization page', () => {
    beforeEach(() => {
      cy.visit(`${authorizationPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Authorization');
    });

    it.skip('should create an instance of Authorization', () => {
      cy.get(`[data-cy="status"]`).type('Sheqel Cambridgeshire invoice').should('have.value', 'Sheqel Cambridgeshire invoice');

      cy.get(`[data-cy="authId"]`).type('payment parse Pennsylvania').should('have.value', 'payment parse Pennsylvania');

      cy.get(`[data-cy="currencyCode"]`).type('AMD').should('have.value', 'AMD');

      cy.get(`[data-cy="amount"]`).type('22581').should('have.value', '22581');

      cy.get(`[data-cy="expirationTime"]`).type('2022-03-17T20:40').should('have.value', '2022-03-17T20:40');

      cy.get(`[data-cy="paymentProvider"]`).select('PAYPAL');

      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        authorization = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', authorizationPageUrlPattern);
    });
  });
});
