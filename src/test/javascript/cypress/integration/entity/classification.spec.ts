import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Classification e2e test', () => {
  const classificationPageUrl = '/classification';
  const classificationPageUrlPattern = new RegExp('/classification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const classificationSample = {};

  let classification: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/classifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/classifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/classifications/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (classification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/classifications/${classification.id}`,
      }).then(() => {
        classification = undefined;
      });
    }
  });

  it('Classifications menu should load Classifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('classification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Classification').should('exist');
    cy.url().should('match', classificationPageUrlPattern);
  });

  describe('Classification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(classificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Classification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/classification/new$'));
        cy.getEntityCreateUpdateHeading('Classification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', classificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/classifications',
          body: classificationSample,
        }).then(({ body }) => {
          classification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/classifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/classifications?page=0&size=20>; rel="last",<http://localhost/api/classifications?page=0&size=20>; rel="first"',
              },
              body: [classification],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(classificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Classification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('classification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', classificationPageUrlPattern);
      });

      it('edit button click should load edit Classification page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Classification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', classificationPageUrlPattern);
      });

      it('last delete button click should delete instance of Classification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('classification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', classificationPageUrlPattern);

        classification = undefined;
      });
    });
  });

  describe('new Classification page', () => {
    beforeEach(() => {
      cy.visit(`${classificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Classification');
    });

    it('should create an instance of Classification', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        classification = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', classificationPageUrlPattern);
    });
  });
});
