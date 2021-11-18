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

describe('OrderItem e2e test', () => {
  const orderItemPageUrl = '/order-item';
  const orderItemPageUrlPattern = new RegExp('/order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const orderItemSample = {
    productName: 'Saint',
    pictureUrl: 'Street Vision-oriented digital',
    unitPrice: 93090,
    discount: 93570,
    units: 17841,
    productId: 56978,
  };

  let orderItem: any;
  //let order: any;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/services/orders/api/order-items').as('entitiesRequest');
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
      body: {"orderDate":"2021-11-13T05:39:58.561Z","orderStatus":"CANCELLED","description":"Colorado"},
    }).then(({ body }) => {
      order = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/services/orders/api/order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/orders/api/order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/services/orders/api/order-items/*').as('deleteEntityRequest');
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
    if (orderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/orders/api/order-items/${orderItem.id}`,
      }).then(() => {
        orderItem = undefined;
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

  it('OrderItems menu should load OrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderItem').should('exist');
    cy.url().should('match', orderItemPageUrlPattern);
  });

  describe('OrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderItem page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/order-item/new$'));
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/orders/api/order-items',
  
          body: {
            ...orderItemSample,
            order: order,
          },
        }).then(({ body }) => {
          orderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/orders/api/order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [orderItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(orderItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details OrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderItem');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('edit button click should load edit OrderItem page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of OrderItem', () => {
        cy.intercept('GET', '/services/orders/api/order-items/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('orderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);

        orderItem = undefined;
      });
    });
  });

  describe('new OrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${orderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('OrderItem');
    });

    it.skip('should create an instance of OrderItem', () => {
      cy.get(`[data-cy="productName"]`).type('Indonesia').should('have.value', 'Indonesia');

      cy.get(`[data-cy="pictureUrl"]`).type('leverage Re-engineered capacity').should('have.value', 'leverage Re-engineered capacity');

      cy.get(`[data-cy="unitPrice"]`).type('278').should('have.value', '278');

      cy.get(`[data-cy="discount"]`).type('93870').should('have.value', '93870');

      cy.get(`[data-cy="units"]`).type('10461').should('have.value', '10461');

      cy.get(`[data-cy="productId"]`).type('68139').should('have.value', '68139');

      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        orderItem = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', orderItemPageUrlPattern);
    });
  });
});
