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

describe('Url e2e test', () => {
  const urlPageUrl = '/url';
  const urlPageUrlPattern = new RegExp('/url(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const urlSample = { url: 'http://audran.info' };

  let url: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/urls+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/urls').as('postEntityRequest');
    cy.intercept('DELETE', '/api/urls/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (url) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/urls/${url.id}`,
      }).then(() => {
        url = undefined;
      });
    }
  });

  it('Urls menu should load Urls page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('url');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Url').should('exist');
    cy.url().should('match', urlPageUrlPattern);
  });

  describe('Url page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(urlPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Url page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/url/new$'));
        cy.getEntityCreateUpdateHeading('Url');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', urlPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/urls',
          body: urlSample,
        }).then(({ body }) => {
          url = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/urls+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/urls?page=0&size=20>; rel="last",<http://localhost/api/urls?page=0&size=20>; rel="first"',
              },
              body: [url],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(urlPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Url page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('url');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', urlPageUrlPattern);
      });

      it('edit button click should load edit Url page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Url');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', urlPageUrlPattern);
      });

      it('last delete button click should delete instance of Url', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('url').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', urlPageUrlPattern);

        url = undefined;
      });
    });
  });

  describe('new Url page', () => {
    beforeEach(() => {
      cy.visit(`${urlPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Url');
    });

    it('should create an instance of Url', () => {
      cy.get(`[data-cy="url"]`).type('http://valère.net').should('have.value', 'http://valère.net');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        url = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', urlPageUrlPattern);
    });
  });
});
