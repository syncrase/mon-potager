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

describe('Reference e2e test', () => {
  const referencePageUrl = '/reference';
  const referencePageUrlPattern = new RegExp('/reference(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const referenceSample = { type: 'IMAGE' };

  let reference: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/references+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/references').as('postEntityRequest');
    cy.intercept('DELETE', '/api/references/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reference) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/references/${reference.id}`,
      }).then(() => {
        reference = undefined;
      });
    }
  });

  it('References menu should load References page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reference');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reference').should('exist');
    cy.url().should('match', referencePageUrlPattern);
  });

  describe('Reference page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(referencePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reference page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reference/new$'));
        cy.getEntityCreateUpdateHeading('Reference');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', referencePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/references',
          body: referenceSample,
        }).then(({ body }) => {
          reference = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/references+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/references?page=0&size=20>; rel="last",<http://localhost/api/references?page=0&size=20>; rel="first"',
              },
              body: [reference],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(referencePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reference page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reference');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', referencePageUrlPattern);
      });

      it('edit button click should load edit Reference page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reference');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', referencePageUrlPattern);
      });

      it('last delete button click should delete instance of Reference', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reference').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', referencePageUrlPattern);

        reference = undefined;
      });
    });
  });

  describe('new Reference page', () => {
    beforeEach(() => {
      cy.visit(`${referencePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reference');
    });

    it('should create an instance of Reference', () => {
      cy.get(`[data-cy="description"]`).type('Buckinghamshire').should('have.value', 'Buckinghamshire');

      cy.get(`[data-cy="type"]`).select('SOURCE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        reference = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', referencePageUrlPattern);
    });
  });
});
