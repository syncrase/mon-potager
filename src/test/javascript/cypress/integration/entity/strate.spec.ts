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

describe('Strate e2e test', () => {
  const stratePageUrl = '/strate';
  const stratePageUrlPattern = new RegExp('/strate(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const strateSample = {};

  let strate: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/strates+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/strates').as('postEntityRequest');
    cy.intercept('DELETE', '/api/strates/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (strate) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/strates/${strate.id}`,
      }).then(() => {
        strate = undefined;
      });
    }
  });

  it('Strates menu should load Strates page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('strate');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Strate').should('exist');
    cy.url().should('match', stratePageUrlPattern);
  });

  describe('Strate page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stratePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Strate page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/strate/new$'));
        cy.getEntityCreateUpdateHeading('Strate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stratePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/strates',
          body: strateSample,
        }).then(({ body }) => {
          strate = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/strates+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/strates?page=0&size=20>; rel="last",<http://localhost/api/strates?page=0&size=20>; rel="first"',
              },
              body: [strate],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stratePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Strate page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('strate');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stratePageUrlPattern);
      });

      it('edit button click should load edit Strate page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Strate');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stratePageUrlPattern);
      });

      it('last delete button click should delete instance of Strate', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('strate').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stratePageUrlPattern);

        strate = undefined;
      });
    });
  });

  describe('new Strate page', () => {
    beforeEach(() => {
      cy.visit(`${stratePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Strate');
    });

    it('should create an instance of Strate', () => {
      cy.get(`[data-cy="type"]`).type('Granite').should('have.value', 'Granite');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        strate = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', stratePageUrlPattern);
    });
  });
});
