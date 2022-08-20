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

describe('Takhtajan e2e test', () => {
  const takhtajanPageUrl = '/takhtajan';
  const takhtajanPageUrlPattern = new RegExp('/takhtajan(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const takhtajanSample = {};

  let takhtajan: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/takhtajans+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/takhtajans').as('postEntityRequest');
    cy.intercept('DELETE', '/api/takhtajans/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (takhtajan) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/takhtajans/${takhtajan.id}`,
      }).then(() => {
        takhtajan = undefined;
      });
    }
  });

  it('Takhtajans menu should load Takhtajans page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('takhtajan');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Takhtajan').should('exist');
    cy.url().should('match', takhtajanPageUrlPattern);
  });

  describe('Takhtajan page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(takhtajanPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Takhtajan page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/takhtajan/new$'));
        cy.getEntityCreateUpdateHeading('Takhtajan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', takhtajanPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/takhtajans',
          body: takhtajanSample,
        }).then(({ body }) => {
          takhtajan = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/takhtajans+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/takhtajans?page=0&size=20>; rel="last",<http://localhost/api/takhtajans?page=0&size=20>; rel="first"',
              },
              body: [takhtajan],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(takhtajanPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Takhtajan page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('takhtajan');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', takhtajanPageUrlPattern);
      });

      it('edit button click should load edit Takhtajan page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Takhtajan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', takhtajanPageUrlPattern);
      });

      it('last delete button click should delete instance of Takhtajan', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('takhtajan').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', takhtajanPageUrlPattern);

        takhtajan = undefined;
      });
    });
  });

  describe('new Takhtajan page', () => {
    beforeEach(() => {
      cy.visit(`${takhtajanPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Takhtajan');
    });

    it('should create an instance of Takhtajan', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        takhtajan = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', takhtajanPageUrlPattern);
    });
  });
});
