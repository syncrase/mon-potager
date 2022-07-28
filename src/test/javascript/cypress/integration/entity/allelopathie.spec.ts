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

describe('Allelopathie e2e test', () => {
  const allelopathiePageUrl = '/allelopathie';
  const allelopathiePageUrlPattern = new RegExp('/allelopathie(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const allelopathieSample = { type: 'bluetooth maximize' };

  let allelopathie: any;
  let plante: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/plantes',
      body: {
        entretien: 'c Danemark',
        histoire: 'Manager middleware Cordoba',
        vitesseCroissance: "d'Abbeville neutral",
        exposition: 'red Fish Grèce',
      },
    }).then(({ body }) => {
      plante = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/allelopathies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/allelopathies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/allelopathies/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/plantes', {
      statusCode: 200,
      body: [plante],
    });
  });

  afterEach(() => {
    if (allelopathie) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/allelopathies/${allelopathie.id}`,
      }).then(() => {
        allelopathie = undefined;
      });
    }
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

  it('Allelopathies menu should load Allelopathies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('allelopathie');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Allelopathie').should('exist');
    cy.url().should('match', allelopathiePageUrlPattern);
  });

  describe('Allelopathie page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(allelopathiePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Allelopathie page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/allelopathie/new$'));
        cy.getEntityCreateUpdateHeading('Allelopathie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', allelopathiePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/allelopathies',
          body: {
            ...allelopathieSample,
            cible: plante,
            origine: plante,
          },
        }).then(({ body }) => {
          allelopathie = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/allelopathies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/allelopathies?page=0&size=20>; rel="last",<http://localhost/api/allelopathies?page=0&size=20>; rel="first"',
              },
              body: [allelopathie],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(allelopathiePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Allelopathie page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('allelopathie');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', allelopathiePageUrlPattern);
      });

      it('edit button click should load edit Allelopathie page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Allelopathie');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', allelopathiePageUrlPattern);
      });

      it('last delete button click should delete instance of Allelopathie', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('allelopathie').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', allelopathiePageUrlPattern);

        allelopathie = undefined;
      });
    });
  });

  describe('new Allelopathie page', () => {
    beforeEach(() => {
      cy.visit(`${allelopathiePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Allelopathie');
    });

    it('should create an instance of Allelopathie', () => {
      cy.get(`[data-cy="type"]`).type('responsive reboot').should('have.value', 'responsive reboot');

      cy.get(`[data-cy="description"]`).type('Rupee').should('have.value', 'Rupee');

      cy.get(`[data-cy="impact"]`).type('6').should('have.value', '6');

      cy.get(`[data-cy="cible"]`).select(1);
      cy.get(`[data-cy="origine"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        allelopathie = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', allelopathiePageUrlPattern);
    });
  });
});
