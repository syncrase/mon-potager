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

describe('Mois e2e test', () => {
  const moisPageUrl = '/mois';
  const moisPageUrlPattern = new RegExp('/mois(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const moisSample = { numero: 34099, nom: 'Islande Toys optimizing' };

  let mois: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mois+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mois').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mois/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mois) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mois/${mois.id}`,
      }).then(() => {
        mois = undefined;
      });
    }
  });

  it('Mois menu should load Mois page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mois');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Mois').should('exist');
    cy.url().should('match', moisPageUrlPattern);
  });

  describe('Mois page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(moisPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Mois page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mois/new$'));
        cy.getEntityCreateUpdateHeading('Mois');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', moisPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mois',
          body: moisSample,
        }).then(({ body }) => {
          mois = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mois+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mois?page=0&size=20>; rel="last",<http://localhost/api/mois?page=0&size=20>; rel="first"',
              },
              body: [mois],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(moisPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Mois page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mois');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', moisPageUrlPattern);
      });

      it('edit button click should load edit Mois page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Mois');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', moisPageUrlPattern);
      });

      it('last delete button click should delete instance of Mois', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mois').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', moisPageUrlPattern);

        mois = undefined;
      });
    });
  });

  describe('new Mois page', () => {
    beforeEach(() => {
      cy.visit(`${moisPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Mois');
    });

    it('should create an instance of Mois', () => {
      cy.get(`[data-cy="numero"]`).type('3426').should('have.value', '3426');

      cy.get(`[data-cy="nom"]`).type('Peso').should('have.value', 'Peso');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        mois = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', moisPageUrlPattern);
    });
  });
});
