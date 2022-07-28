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

describe('Plante e2e test', () => {
  const plantePageUrl = '/plante';
  const plantePageUrlPattern = new RegExp('/plante(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const planteSample = {};

  let plante: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/plantes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/plantes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/plantes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (plante) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/plantes/${plante.id}`,
      }).then(() => {
        plante = undefined;
      });
    }
  });

  it('Plantes menu should load Plantes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('plante');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Plante').should('exist');
    cy.url().should('match', plantePageUrlPattern);
  });

  describe('Plante page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(plantePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Plante page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/plante/new$'));
        cy.getEntityCreateUpdateHeading('Plante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/plantes',
          body: planteSample,
        }).then(({ body }) => {
          plante = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/plantes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/plantes?page=0&size=20>; rel="last",<http://localhost/api/plantes?page=0&size=20>; rel="first"',
              },
              body: [plante],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(plantePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Plante page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('plante');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });

      it('edit button click should load edit Plante page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Plante');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);
      });

      it('last delete button click should delete instance of Plante', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('plante').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', plantePageUrlPattern);

        plante = undefined;
      });
    });
  });

  describe('new Plante page', () => {
    beforeEach(() => {
      cy.visit(`${plantePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Plante');
    });

    it('should create an instance of Plante', () => {
      cy.get(`[data-cy="entretien"]`).type('Saint-Marin quantifying').should('have.value', 'Saint-Marin quantifying');

      cy.get(`[data-cy="histoire"]`).type('deposit Haute-Normandie Chicken').should('have.value', 'deposit Haute-Normandie Chicken');

      cy.get(`[data-cy="vitesseCroissance"]`).type('generating').should('have.value', 'generating');

      cy.get(`[data-cy="exposition"]`).type('synthesize Games Loan').should('have.value', 'synthesize Games Loan');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        plante = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', plantePageUrlPattern);
    });
  });
});
