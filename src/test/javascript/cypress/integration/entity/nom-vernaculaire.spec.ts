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

describe('NomVernaculaire e2e test', () => {
  const nomVernaculairePageUrl = '/nom-vernaculaire';
  const nomVernaculairePageUrlPattern = new RegExp('/nom-vernaculaire(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const nomVernaculaireSample = {};

  let nomVernaculaire: any;
  //let cronquistRank: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/cronquist-ranks',
      body: {"rank":"SUPERFAMILLE"},
    }).then(({ body }) => {
      cronquistRank = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/nom-vernaculaires+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/nom-vernaculaires').as('postEntityRequest');
    cy.intercept('DELETE', '/api/nom-vernaculaires/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/cronquist-ranks', {
      statusCode: 200,
      body: [cronquistRank],
    });

    cy.intercept('GET', '/api/plantes', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (nomVernaculaire) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/nom-vernaculaires/${nomVernaculaire.id}`,
      }).then(() => {
        nomVernaculaire = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (cronquistRank) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cronquist-ranks/${cronquistRank.id}`,
      }).then(() => {
        cronquistRank = undefined;
      });
    }
  });
   */

  it('NomVernaculaires menu should load NomVernaculaires page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('nom-vernaculaire');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('NomVernaculaire').should('exist');
    cy.url().should('match', nomVernaculairePageUrlPattern);
  });

  describe('NomVernaculaire page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(nomVernaculairePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create NomVernaculaire page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/nom-vernaculaire/new$'));
        cy.getEntityCreateUpdateHeading('NomVernaculaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', nomVernaculairePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/nom-vernaculaires',
          body: {
            ...nomVernaculaireSample,
            cronquistRank: cronquistRank,
          },
        }).then(({ body }) => {
          nomVernaculaire = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/nom-vernaculaires+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/nom-vernaculaires?page=0&size=20>; rel="last",<http://localhost/api/nom-vernaculaires?page=0&size=20>; rel="first"',
              },
              body: [nomVernaculaire],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(nomVernaculairePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(nomVernaculairePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details NomVernaculaire page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('nomVernaculaire');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', nomVernaculairePageUrlPattern);
      });

      it('edit button click should load edit NomVernaculaire page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NomVernaculaire');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', nomVernaculairePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of NomVernaculaire', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('nomVernaculaire').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', nomVernaculairePageUrlPattern);

        nomVernaculaire = undefined;
      });
    });
  });

  describe('new NomVernaculaire page', () => {
    beforeEach(() => {
      cy.visit(`${nomVernaculairePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('NomVernaculaire');
    });

    it.skip('should create an instance of NomVernaculaire', () => {
      cy.get(`[data-cy="nom"]`).type('quantify').should('have.value', 'quantify');

      cy.get(`[data-cy="cronquistRank"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        nomVernaculaire = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', nomVernaculairePageUrlPattern);
    });
  });
});
