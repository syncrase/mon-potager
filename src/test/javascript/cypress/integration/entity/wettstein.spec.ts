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

describe('Wettstein e2e test', () => {
  const wettsteinPageUrl = '/wettstein';
  const wettsteinPageUrlPattern = new RegExp('/wettstein(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const wettsteinSample = {};

  let wettstein: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/wettsteins+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/wettsteins').as('postEntityRequest');
    cy.intercept('DELETE', '/api/wettsteins/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (wettstein) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/wettsteins/${wettstein.id}`,
      }).then(() => {
        wettstein = undefined;
      });
    }
  });

  it('Wettsteins menu should load Wettsteins page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('wettstein');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Wettstein').should('exist');
    cy.url().should('match', wettsteinPageUrlPattern);
  });

  describe('Wettstein page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(wettsteinPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Wettstein page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/wettstein/new$'));
        cy.getEntityCreateUpdateHeading('Wettstein');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', wettsteinPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/wettsteins',
          body: wettsteinSample,
        }).then(({ body }) => {
          wettstein = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/wettsteins+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/wettsteins?page=0&size=20>; rel="last",<http://localhost/api/wettsteins?page=0&size=20>; rel="first"',
              },
              body: [wettstein],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(wettsteinPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Wettstein page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('wettstein');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', wettsteinPageUrlPattern);
      });

      it('edit button click should load edit Wettstein page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Wettstein');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', wettsteinPageUrlPattern);
      });

      it('last delete button click should delete instance of Wettstein', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('wettstein').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', wettsteinPageUrlPattern);

        wettstein = undefined;
      });
    });
  });

  describe('new Wettstein page', () => {
    beforeEach(() => {
      cy.visit(`${wettsteinPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Wettstein');
    });

    it('should create an instance of Wettstein', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        wettstein = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', wettsteinPageUrlPattern);
    });
  });
});
