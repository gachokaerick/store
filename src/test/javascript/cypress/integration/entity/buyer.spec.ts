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

describe('Buyer e2e test', () => {
  const buyerPageUrl = '/buyer';
  const buyerPageUrlPattern = new RegExp('/buyer(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const buyerSample = { firstName: 'Nedra', lastName: 'Weimann', gender: 'FEMALE', email: 'yh1eQ_@-Wd;%.Bk', phone: '453-970-9437 x878' };

  let buyer: any;
  //let user: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/buyers').as('entitiesRequest');
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
      url: '/api/users',
      body: {"id":"2b2aafc0-7023-4433-aeda-36def0a31030","login":"deposit orchestration","firstName":"Carlee","lastName":"Fisher"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/buyers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/buyers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/buyers/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/orders', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (buyer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/buyers/${buyer.id}`,
      }).then(() => {
        buyer = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('Buyers menu should load Buyers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('buyer');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Buyer').should('exist');
    cy.url().should('match', buyerPageUrlPattern);
  });

  describe('Buyer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(buyerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Buyer page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/buyer/new$'));
        cy.getEntityCreateUpdateHeading('Buyer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', buyerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/buyers',
  
          body: {
            ...buyerSample,
            user: user,
          },
        }).then(({ body }) => {
          buyer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/buyers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [buyer],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(buyerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(buyerPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Buyer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('buyer');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', buyerPageUrlPattern);
      });

      it('edit button click should load edit Buyer page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Buyer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', buyerPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Buyer', () => {
        cy.intercept('GET', '/api/buyers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('buyer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', buyerPageUrlPattern);

        buyer = undefined;
      });
    });
  });

  describe('new Buyer page', () => {
    beforeEach(() => {
      cy.visit(`${buyerPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Buyer');
    });

    it.skip('should create an instance of Buyer', () => {
      cy.get(`[data-cy="firstName"]`).type('Quinn').should('have.value', 'Quinn');

      cy.get(`[data-cy="lastName"]`).type('Braun').should('have.value', 'Braun');

      cy.get(`[data-cy="gender"]`).select('MALE');

      cy.get(`[data-cy="email"]`).type('et$o@d]&lt;9._').should('have.value', 'et$o@d]&lt;9._');

      cy.get(`[data-cy="phone"]`).type('261-693-2393').should('have.value', '261-693-2393');

      cy.get(`[data-cy="user"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        buyer = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', buyerPageUrlPattern);
    });
  });
});
