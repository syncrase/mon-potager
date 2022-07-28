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

describe('CycleDeVie e2e test', () => {
  const cycleDeViePageUrl = '/cycle-de-vie';
  const cycleDeViePageUrlPattern = new RegExp('/cycle-de-vie(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const cycleDeVieSample = {};

  let cycleDeVie: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/cycle-de-vies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cycle-de-vies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cycle-de-vies/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (cycleDeVie) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cycle-de-vies/${cycleDeVie.id}`,
      }).then(() => {
        cycleDeVie = undefined;
      });
    }
  });

  it('CycleDeVies menu should load CycleDeVies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cycle-de-vie');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CycleDeVie').should('exist');
    cy.url().should('match', cycleDeViePageUrlPattern);
  });

  describe('CycleDeVie page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cycleDeViePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CycleDeVie page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cycle-de-vie/new$'));
        cy.getEntityCreateUpdateHeading('CycleDeVie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cycleDeViePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cycle-de-vies',
          body: cycleDeVieSample,
        }).then(({ body }) => {
          cycleDeVie = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cycle-de-vies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cycle-de-vies?page=0&size=20>; rel="last",<http://localhost/api/cycle-de-vies?page=0&size=20>; rel="first"',
              },
              body: [cycleDeVie],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(cycleDeViePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CycleDeVie page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('cycleDeVie');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cycleDeViePageUrlPattern);
      });

      it('edit button click should load edit CycleDeVie page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CycleDeVie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cycleDeViePageUrlPattern);
      });

      it('last delete button click should delete instance of CycleDeVie', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('cycleDeVie').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cycleDeViePageUrlPattern);

        cycleDeVie = undefined;
      });
    });
  });

  describe('new CycleDeVie page', () => {
    beforeEach(() => {
      cy.visit(`${cycleDeViePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CycleDeVie');
    });

    it('should create an instance of CycleDeVie', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        cycleDeVie = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', cycleDeViePageUrlPattern);
    });
  });
});
