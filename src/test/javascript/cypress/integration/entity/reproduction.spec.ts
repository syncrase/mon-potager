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

describe('Reproduction e2e test', () => {
  const reproductionPageUrl = '/reproduction';
  const reproductionPageUrlPattern = new RegExp('/reproduction(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reproductionSample = {};

  let reproduction: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reproductions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reproductions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reproductions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reproduction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reproductions/${reproduction.id}`,
      }).then(() => {
        reproduction = undefined;
      });
    }
  });

  it('Reproductions menu should load Reproductions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reproduction');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reproduction').should('exist');
    cy.url().should('match', reproductionPageUrlPattern);
  });

  describe('Reproduction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reproductionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reproduction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reproduction/new$'));
        cy.getEntityCreateUpdateHeading('Reproduction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', reproductionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reproductions',
          body: reproductionSample,
        }).then(({ body }) => {
          reproduction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reproductions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/reproductions?page=0&size=20>; rel="last",<http://localhost/api/reproductions?page=0&size=20>; rel="first"',
              },
              body: [reproduction],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(reproductionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reproduction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reproduction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', reproductionPageUrlPattern);
      });

      it('edit button click should load edit Reproduction page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reproduction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', reproductionPageUrlPattern);
      });

      it('last delete button click should delete instance of Reproduction', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reproduction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', reproductionPageUrlPattern);

        reproduction = undefined;
      });
    });
  });

  describe('new Reproduction page', () => {
    beforeEach(() => {
      cy.visit(`${reproductionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reproduction');
    });

    it('should create an instance of Reproduction', () => {
      cy.get(`[data-cy="vitesse"]`).type('wireless Borders').should('have.value', 'wireless Borders');

      cy.get(`[data-cy="type"]`).type('Auto distributed b').should('have.value', 'Auto distributed b');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        reproduction = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', reproductionPageUrlPattern);
    });
  });
});
