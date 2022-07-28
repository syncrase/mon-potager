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

describe('Feuillage e2e test', () => {
  const feuillagePageUrl = '/feuillage';
  const feuillagePageUrlPattern = new RegExp('/feuillage(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const feuillageSample = {};

  let feuillage: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/feuillages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/feuillages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/feuillages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (feuillage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/feuillages/${feuillage.id}`,
      }).then(() => {
        feuillage = undefined;
      });
    }
  });

  it('Feuillages menu should load Feuillages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('feuillage');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Feuillage').should('exist');
    cy.url().should('match', feuillagePageUrlPattern);
  });

  describe('Feuillage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(feuillagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Feuillage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/feuillage/new$'));
        cy.getEntityCreateUpdateHeading('Feuillage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', feuillagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/feuillages',
          body: feuillageSample,
        }).then(({ body }) => {
          feuillage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/feuillages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/feuillages?page=0&size=20>; rel="last",<http://localhost/api/feuillages?page=0&size=20>; rel="first"',
              },
              body: [feuillage],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(feuillagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Feuillage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('feuillage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', feuillagePageUrlPattern);
      });

      it('edit button click should load edit Feuillage page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Feuillage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', feuillagePageUrlPattern);
      });

      it('last delete button click should delete instance of Feuillage', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('feuillage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', feuillagePageUrlPattern);

        feuillage = undefined;
      });
    });
  });

  describe('new Feuillage page', () => {
    beforeEach(() => {
      cy.visit(`${feuillagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Feuillage');
    });

    it('should create an instance of Feuillage', () => {
      cy.get(`[data-cy="type"]`).type('card fault-tolerant emulation').should('have.value', 'card fault-tolerant emulation');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        feuillage = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', feuillagePageUrlPattern);
    });
  });
});
