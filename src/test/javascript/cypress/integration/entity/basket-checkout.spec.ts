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

describe('BasketCheckout e2e test', () => {
  const basketCheckoutPageUrl = '/basket-checkout';
  const basketCheckoutPageUrlPattern = new RegExp('/basket-checkout(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const basketCheckoutSample = {
    city: 'North Goldenview',
    town: 'rich Virginia',
    country: 'Liberia',
    createTime: '2021-11-14T01:11:44.743Z',
    updateTime: '2021-11-13T11:52:39.920Z',
    payerName: 'quantify Associate Monaco',
    payerId: 'fuchsia calculate withdrawal',
    currency: 'engage',
    amount: 54735,
    userLogin: 'connecting system',
  };

  let basketCheckout: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/basket/api/basket-checkouts').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    Cypress.Cookies.preserveOnce('XSRF-TOKEN', 'JSESSIONID');
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/basket/api/basket-checkouts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/basket/api/basket-checkouts').as('postEntityRequest');
    cy.intercept('DELETE', '/services/basket/api/basket-checkouts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (basketCheckout) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/basket/api/basket-checkouts/${basketCheckout.id}`,
      }).then(() => {
        basketCheckout = undefined;
      });
    }
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  it('BasketCheckouts menu should load BasketCheckouts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('basket-checkout');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BasketCheckout').should('exist');
    cy.url().should('match', basketCheckoutPageUrlPattern);
  });

  describe('BasketCheckout page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(basketCheckoutPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BasketCheckout page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/basket-checkout/new$'));
        cy.getEntityCreateUpdateHeading('BasketCheckout');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketCheckoutPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/basket/api/basket-checkouts',
          body: basketCheckoutSample,
        }).then(({ body }) => {
          basketCheckout = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/basket/api/basket-checkouts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [basketCheckout],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(basketCheckoutPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BasketCheckout page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('basketCheckout');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketCheckoutPageUrlPattern);
      });

      it('edit button click should load edit BasketCheckout page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BasketCheckout');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketCheckoutPageUrlPattern);
      });

      it('last delete button click should delete instance of BasketCheckout', () => {
        cy.intercept('GET', '/services/basket/api/basket-checkouts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('basketCheckout').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', basketCheckoutPageUrlPattern);

        basketCheckout = undefined;
      });
    });
  });

  describe('new BasketCheckout page', () => {
    beforeEach(() => {
      cy.visit(`${basketCheckoutPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('BasketCheckout');
    });

    it('should create an instance of BasketCheckout', () => {
      cy.get(`[data-cy="street"]`).type('Freeman Vista').should('have.value', 'Freeman Vista');

      cy.get(`[data-cy="city"]`).type('North Tierraburgh').should('have.value', 'North Tierraburgh');

      cy.get(`[data-cy="town"]`).type('Assistant').should('have.value', 'Assistant');

      cy.get(`[data-cy="country"]`).type('Hungary').should('have.value', 'Hungary');

      cy.get(`[data-cy="zipcode"]`).type('Rubber').should('have.value', 'Rubber');

      cy.get(`[data-cy="createTime"]`).type('2021-11-13T07:46').should('have.value', '2021-11-13T07:46');

      cy.get(`[data-cy="updateTime"]`).type('2021-11-13T20:10').should('have.value', '2021-11-13T20:10');

      cy.get(`[data-cy="paymentStatus"]`).type('Wooden').should('have.value', 'Wooden');

      cy.get(`[data-cy="payerCountryCode"]`).type('CSS forecast').should('have.value', 'CSS forecast');

      cy.get(`[data-cy="payerEmail"]`).type('connecting').should('have.value', 'connecting');

      cy.get(`[data-cy="payerName"]`).type('Right-sized programming').should('have.value', 'Right-sized programming');

      cy.get(`[data-cy="payerSurname"]`).type('Ergonomic azure Pants').should('have.value', 'Ergonomic azure Pants');

      cy.get(`[data-cy="payerId"]`).type('withdrawal Facilitator').should('have.value', 'withdrawal Facilitator');

      cy.get(`[data-cy="currency"]`).type('copying drive').should('have.value', 'copying drive');

      cy.get(`[data-cy="amount"]`).type('2360').should('have.value', '2360');

      cy.get(`[data-cy="paymentId"]`).type('bypass synergy neural').should('have.value', 'bypass synergy neural');

      cy.get(`[data-cy="userLogin"]`).type('XML').should('have.value', 'XML');

      cy.get(`[data-cy="description"]`).type('Planner Ball SAS').should('have.value', 'Planner Ball SAS');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        basketCheckout = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', basketCheckoutPageUrlPattern);
    });
  });
});
