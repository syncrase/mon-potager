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

describe('TypeSemis e2e test', () => {
  const typeSemisPageUrl = '/type-semis';
  const typeSemisPageUrlPattern = new RegExp('/type-semis(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const typeSemisSample = { type: 'Manager withdrawal Shirt' };

  let typeSemis: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/type-semis+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/type-semis').as('postEntityRequest');
    cy.intercept('DELETE', '/api/type-semis/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (typeSemis) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/type-semis/${typeSemis.id}`,
      }).then(() => {
        typeSemis = undefined;
      });
    }
  });

  it('TypeSemis menu should load TypeSemis page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('type-semis');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TypeSemis').should('exist');
    cy.url().should('match', typeSemisPageUrlPattern);
  });

  describe('TypeSemis page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(typeSemisPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TypeSemis page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/type-semis/new$'));
        cy.getEntityCreateUpdateHeading('TypeSemis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', typeSemisPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/type-semis',
          body: typeSemisSample,
        }).then(({ body }) => {
          typeSemis = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/type-semis+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/type-semis?page=0&size=20>; rel="last",<http://localhost/api/type-semis?page=0&size=20>; rel="first"',
              },
              body: [typeSemis],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(typeSemisPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TypeSemis page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('typeSemis');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', typeSemisPageUrlPattern);
      });

      it('edit button click should load edit TypeSemis page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TypeSemis');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', typeSemisPageUrlPattern);
      });

      it('last delete button click should delete instance of TypeSemis', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('typeSemis').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', typeSemisPageUrlPattern);

        typeSemis = undefined;
      });
    });
  });

  describe('new TypeSemis page', () => {
    beforeEach(() => {
      cy.visit(`${typeSemisPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TypeSemis');
    });

    it('should create an instance of TypeSemis', () => {
      cy.get(`[data-cy="type"]`).type('reinvent bandwidth multi-byte').should('have.value', 'reinvent bandwidth multi-byte');

      cy.get(`[data-cy="description"]`).type('Sharable').should('have.value', 'Sharable');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        typeSemis = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', typeSemisPageUrlPattern);
    });
  });
});
