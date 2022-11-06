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

describe('Address e2e test', () => {
  const addressPageUrl = '/address';
  const addressPageUrlPattern = new RegExp('/address(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const addressSample = { city: 'Port Mayabury', town: 'Directives Pants', country: 'Saint Vincent and the Grenadines' };

  let address: any;
  //let buyer: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/orders/api/addresses').as('entitiesRequest');
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
      url: '/services/orders/api/buyers',
      body: {"firstName":"Sienna","lastName":"Rath","gender":"OTHER","email":"d<;b|@;{tAJM.Na","phone":"1-978-207-3065 x439"},
    }).then(({ body }) => {
      buyer = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/services/orders/api/addresses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/orders/api/addresses').as('postEntityRequest');
    cy.intercept('DELETE', '/services/orders/api/addresses/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/services/orders/api/buyers', {
      statusCode: 200,
      body: [buyer],
    });

  });
   */

  afterEach(() => {
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/orders/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (buyer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/orders/api/buyers/${buyer.id}`,
      }).then(() => {
        buyer = undefined;
      });
    }
  });
   */

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('Addresses menu should load Addresses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('address');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Address').should('exist');
    cy.url().should('match', addressPageUrlPattern);
  });

  describe('Address page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(addressPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Address page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/address/new$'));
        cy.getEntityCreateUpdateHeading('Address');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', addressPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/orders/api/addresses',
  
          body: {
            ...addressSample,
            buyer: buyer,
          },
        }).then(({ body }) => {
          address = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/orders/api/addresses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [address],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(addressPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(addressPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Address page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('address');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', addressPageUrlPattern);
      });

      it('edit button click should load edit Address page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Address');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', addressPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Address', () => {
        cy.intercept('GET', '/services/orders/api/addresses/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('address').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', addressPageUrlPattern);

        address = undefined;
      });
    });
  });

  describe('new Address page', () => {
    beforeEach(() => {
      cy.visit(`${addressPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Address');
    });

    it.skip('should create an instance of Address', () => {
      cy.get(`[data-cy="street"]`).type('Stroman Row').should('have.value', 'Stroman Row');

      cy.get(`[data-cy="city"]`).type('Russelstad').should('have.value', 'Russelstad');

      cy.get(`[data-cy="town"]`).type('Fresh Cotton').should('have.value', 'Fresh Cotton');

      cy.get(`[data-cy="country"]`).type('Zambia').should('have.value', 'Zambia');

      cy.get(`[data-cy="zipcode"]`).type('Montana Forward').should('have.value', 'Montana Forward');

      cy.get(`[data-cy="buyer"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        address = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', addressPageUrlPattern);
    });
  });
});
