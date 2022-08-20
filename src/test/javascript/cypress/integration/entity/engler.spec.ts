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

describe('Engler e2e test', () => {
  const englerPageUrl = '/engler';
  const englerPageUrlPattern = new RegExp('/engler(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const englerSample = {};

  let engler: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/englers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/englers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/englers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (engler) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/englers/${engler.id}`,
      }).then(() => {
        engler = undefined;
      });
    }
  });

  it('Englers menu should load Englers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('engler');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Engler').should('exist');
    cy.url().should('match', englerPageUrlPattern);
  });

  describe('Engler page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(englerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Engler page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/engler/new$'));
        cy.getEntityCreateUpdateHeading('Engler');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', englerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/englers',
          body: englerSample,
        }).then(({ body }) => {
          engler = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/englers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/englers?page=0&size=20>; rel="last",<http://localhost/api/englers?page=0&size=20>; rel="first"',
              },
              body: [engler],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(englerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Engler page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('engler');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', englerPageUrlPattern);
      });

      it('edit button click should load edit Engler page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Engler');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', englerPageUrlPattern);
      });

      it('last delete button click should delete instance of Engler', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('engler').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', englerPageUrlPattern);

        engler = undefined;
      });
    });
  });

  describe('new Engler page', () => {
    beforeEach(() => {
      cy.visit(`${englerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Engler');
    });

    it('should create an instance of Engler', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        engler = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', englerPageUrlPattern);
    });
  });
});
