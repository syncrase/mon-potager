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

describe('Semis e2e test', () => {
  const semisPageUrl = '/semis';
  const semisPageUrlPattern = new RegExp('/semis(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const semisSample = {};

  let semis: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/semis+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/semis').as('postEntityRequest');
    cy.intercept('DELETE', '/api/semis/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (semis) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/semis/${semis.id}`,
      }).then(() => {
        semis = undefined;
      });
    }
  });

  it('Semis menu should load Semis page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('semis');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Semis').should('exist');
    cy.url().should('match', semisPageUrlPattern);
  });

  describe('Semis page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(semisPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Semis page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/semis/new$'));
        cy.getEntityCreateUpdateHeading('Semis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', semisPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/semis',
          body: semisSample,
        }).then(({ body }) => {
          semis = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/semis+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/semis?page=0&size=20>; rel="last",<http://localhost/api/semis?page=0&size=20>; rel="first"',
              },
              body: [semis],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(semisPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Semis page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('semis');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', semisPageUrlPattern);
      });

      it('edit button click should load edit Semis page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Semis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', semisPageUrlPattern);
      });

      it('last delete button click should delete instance of Semis', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('semis').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', semisPageUrlPattern);

        semis = undefined;
      });
    });
  });

  describe('new Semis page', () => {
    beforeEach(() => {
      cy.visit(`${semisPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Semis');
    });

    it('should create an instance of Semis', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        semis = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', semisPageUrlPattern);
    });
  });
});
