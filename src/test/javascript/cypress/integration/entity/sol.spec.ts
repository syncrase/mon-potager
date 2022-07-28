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

describe('Sol e2e test', () => {
  const solPageUrl = '/sol';
  const solPageUrlPattern = new RegExp('/sol(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const solSample = {};

  let sol: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/sols+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sols').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sols/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (sol) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sols/${sol.id}`,
      }).then(() => {
        sol = undefined;
      });
    }
  });

  it('Sols menu should load Sols page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sol');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Sol').should('exist');
    cy.url().should('match', solPageUrlPattern);
  });

  describe('Sol page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(solPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Sol page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sol/new$'));
        cy.getEntityCreateUpdateHeading('Sol');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', solPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sols',
          body: solSample,
        }).then(({ body }) => {
          sol = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sols+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/sols?page=0&size=20>; rel="last",<http://localhost/api/sols?page=0&size=20>; rel="first"',
              },
              body: [sol],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(solPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Sol page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('sol');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', solPageUrlPattern);
      });

      it('edit button click should load edit Sol page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Sol');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', solPageUrlPattern);
      });

      it('last delete button click should delete instance of Sol', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('sol').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', solPageUrlPattern);

        sol = undefined;
      });
    });
  });

  describe('new Sol page', () => {
    beforeEach(() => {
      cy.visit(`${solPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Sol');
    });

    it('should create an instance of Sol', () => {
      cy.get(`[data-cy="phMin"]`).type('41393').should('have.value', '41393');

      cy.get(`[data-cy="phMax"]`).type('83268').should('have.value', '83268');

      cy.get(`[data-cy="type"]`).type('Automated').should('have.value', 'Automated');

      cy.get(`[data-cy="richesse"]`).type('quantifying quantifying Generic').should('have.value', 'quantifying quantifying Generic');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        sol = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', solPageUrlPattern);
    });
  });
});
