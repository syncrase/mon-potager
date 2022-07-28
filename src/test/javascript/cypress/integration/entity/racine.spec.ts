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

describe('Racine e2e test', () => {
  const racinePageUrl = '/racine';
  const racinePageUrlPattern = new RegExp('/racine(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const racineSample = {};

  let racine: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/racines+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/racines').as('postEntityRequest');
    cy.intercept('DELETE', '/api/racines/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (racine) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/racines/${racine.id}`,
      }).then(() => {
        racine = undefined;
      });
    }
  });

  it('Racines menu should load Racines page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('racine');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Racine').should('exist');
    cy.url().should('match', racinePageUrlPattern);
  });

  describe('Racine page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(racinePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Racine page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/racine/new$'));
        cy.getEntityCreateUpdateHeading('Racine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', racinePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/racines',
          body: racineSample,
        }).then(({ body }) => {
          racine = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/racines+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/racines?page=0&size=20>; rel="last",<http://localhost/api/racines?page=0&size=20>; rel="first"',
              },
              body: [racine],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(racinePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Racine page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('racine');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', racinePageUrlPattern);
      });

      it('edit button click should load edit Racine page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Racine');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', racinePageUrlPattern);
      });

      it('last delete button click should delete instance of Racine', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('racine').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', racinePageUrlPattern);

        racine = undefined;
      });
    });
  });

  describe('new Racine page', () => {
    beforeEach(() => {
      cy.visit(`${racinePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Racine');
    });

    it('should create an instance of Racine', () => {
      cy.get(`[data-cy="type"]`).type('Refined Account Chair').should('have.value', 'Refined Account Chair');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        racine = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', racinePageUrlPattern);
    });
  });
});
