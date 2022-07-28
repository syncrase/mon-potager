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

describe('Ressemblance e2e test', () => {
  const ressemblancePageUrl = '/ressemblance';
  const ressemblancePageUrlPattern = new RegExp('/ressemblance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ressemblanceSample = {};

  let ressemblance: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ressemblances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ressemblances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ressemblances/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ressemblance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ressemblances/${ressemblance.id}`,
      }).then(() => {
        ressemblance = undefined;
      });
    }
  });

  it('Ressemblances menu should load Ressemblances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ressemblance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ressemblance').should('exist');
    cy.url().should('match', ressemblancePageUrlPattern);
  });

  describe('Ressemblance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ressemblancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ressemblance page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ressemblance/new$'));
        cy.getEntityCreateUpdateHeading('Ressemblance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ressemblancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ressemblances',
          body: ressemblanceSample,
        }).then(({ body }) => {
          ressemblance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ressemblances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ressemblances?page=0&size=20>; rel="last",<http://localhost/api/ressemblances?page=0&size=20>; rel="first"',
              },
              body: [ressemblance],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ressemblancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Ressemblance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ressemblance');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ressemblancePageUrlPattern);
      });

      it('edit button click should load edit Ressemblance page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ressemblance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ressemblancePageUrlPattern);
      });

      it('last delete button click should delete instance of Ressemblance', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ressemblance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ressemblancePageUrlPattern);

        ressemblance = undefined;
      });
    });
  });

  describe('new Ressemblance page', () => {
    beforeEach(() => {
      cy.visit(`${ressemblancePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ressemblance');
    });

    it('should create an instance of Ressemblance', () => {
      cy.get(`[data-cy="description"]`).type('Rubber Vaneau Designer').should('have.value', 'Rubber Vaneau Designer');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        ressemblance = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', ressemblancePageUrlPattern);
    });
  });
});
