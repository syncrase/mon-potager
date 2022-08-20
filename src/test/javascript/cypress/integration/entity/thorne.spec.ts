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

describe('Thorne e2e test', () => {
  const thornePageUrl = '/thorne';
  const thornePageUrlPattern = new RegExp('/thorne(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const thorneSample = {};

  let thorne: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/thornes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/thornes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/thornes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (thorne) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/thornes/${thorne.id}`,
      }).then(() => {
        thorne = undefined;
      });
    }
  });

  it('Thornes menu should load Thornes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('thorne');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Thorne').should('exist');
    cy.url().should('match', thornePageUrlPattern);
  });

  describe('Thorne page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(thornePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Thorne page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/thorne/new$'));
        cy.getEntityCreateUpdateHeading('Thorne');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', thornePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/thornes',
          body: thorneSample,
        }).then(({ body }) => {
          thorne = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/thornes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/thornes?page=0&size=20>; rel="last",<http://localhost/api/thornes?page=0&size=20>; rel="first"',
              },
              body: [thorne],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(thornePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Thorne page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('thorne');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', thornePageUrlPattern);
      });

      it('edit button click should load edit Thorne page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Thorne');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', thornePageUrlPattern);
      });

      it('last delete button click should delete instance of Thorne', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('thorne').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', thornePageUrlPattern);

        thorne = undefined;
      });
    });
  });

  describe('new Thorne page', () => {
    beforeEach(() => {
      cy.visit(`${thornePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Thorne');
    });

    it('should create an instance of Thorne', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        thorne = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', thornePageUrlPattern);
    });
  });
});
