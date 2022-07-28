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

describe('Ensoleillement e2e test', () => {
  const ensoleillementPageUrl = '/ensoleillement';
  const ensoleillementPageUrlPattern = new RegExp('/ensoleillement(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ensoleillementSample = {};

  let ensoleillement: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ensoleillements+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ensoleillements').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ensoleillements/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ensoleillement) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ensoleillements/${ensoleillement.id}`,
      }).then(() => {
        ensoleillement = undefined;
      });
    }
  });

  it('Ensoleillements menu should load Ensoleillements page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ensoleillement');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ensoleillement').should('exist');
    cy.url().should('match', ensoleillementPageUrlPattern);
  });

  describe('Ensoleillement page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ensoleillementPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ensoleillement page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ensoleillement/new$'));
        cy.getEntityCreateUpdateHeading('Ensoleillement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ensoleillementPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ensoleillements',
          body: ensoleillementSample,
        }).then(({ body }) => {
          ensoleillement = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ensoleillements+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ensoleillements?page=0&size=20>; rel="last",<http://localhost/api/ensoleillements?page=0&size=20>; rel="first"',
              },
              body: [ensoleillement],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ensoleillementPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Ensoleillement page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ensoleillement');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ensoleillementPageUrlPattern);
      });

      it('edit button click should load edit Ensoleillement page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ensoleillement');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ensoleillementPageUrlPattern);
      });

      it('last delete button click should delete instance of Ensoleillement', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ensoleillement').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', ensoleillementPageUrlPattern);

        ensoleillement = undefined;
      });
    });
  });

  describe('new Ensoleillement page', () => {
    beforeEach(() => {
      cy.visit(`${ensoleillementPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ensoleillement');
    });

    it('should create an instance of Ensoleillement', () => {
      cy.get(`[data-cy="orientation"]`).type('Sleek Handcrafted Baby').should('have.value', 'Sleek Handcrafted Baby');

      cy.get(`[data-cy="ensoleilement"]`).type('27791').should('have.value', '27791');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        ensoleillement = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', ensoleillementPageUrlPattern);
    });
  });
});
