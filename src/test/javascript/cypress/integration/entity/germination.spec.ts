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

describe('Germination e2e test', () => {
  const germinationPageUrl = '/germination';
  const germinationPageUrlPattern = new RegExp('/germination(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const germinationSample = {};

  let germination: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/germinations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/germinations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/germinations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (germination) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/germinations/${germination.id}`,
      }).then(() => {
        germination = undefined;
      });
    }
  });

  it('Germinations menu should load Germinations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('germination');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Germination').should('exist');
    cy.url().should('match', germinationPageUrlPattern);
  });

  describe('Germination page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(germinationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Germination page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/germination/new$'));
        cy.getEntityCreateUpdateHeading('Germination');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', germinationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/germinations',
          body: germinationSample,
        }).then(({ body }) => {
          germination = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/germinations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/germinations?page=0&size=20>; rel="last",<http://localhost/api/germinations?page=0&size=20>; rel="first"',
              },
              body: [germination],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(germinationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Germination page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('germination');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', germinationPageUrlPattern);
      });

      it('edit button click should load edit Germination page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Germination');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', germinationPageUrlPattern);
      });

      it('last delete button click should delete instance of Germination', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('germination').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', germinationPageUrlPattern);

        germination = undefined;
      });
    });
  });

  describe('new Germination page', () => {
    beforeEach(() => {
      cy.visit(`${germinationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Germination');
    });

    it('should create an instance of Germination', () => {
      cy.get(`[data-cy="tempsDeGermination"]`).type('Charlemagne Plastic').should('have.value', 'Charlemagne Plastic');

      cy.get(`[data-cy="conditionDeGermination"]`).type('compelling auxiliary').should('have.value', 'compelling auxiliary');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        germination = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', germinationPageUrlPattern);
    });
  });
});
