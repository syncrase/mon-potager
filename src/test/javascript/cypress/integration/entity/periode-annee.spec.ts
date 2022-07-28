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

describe('PeriodeAnnee e2e test', () => {
  const periodeAnneePageUrl = '/periode-annee';
  const periodeAnneePageUrlPattern = new RegExp('/periode-annee(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const periodeAnneeSample = {};

  let periodeAnnee: any;
  let mois: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/mois',
      body: { numero: 11770, nom: 'Guinea Dollar' },
    }).then(({ body }) => {
      mois = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/periode-annees+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/periode-annees').as('postEntityRequest');
    cy.intercept('DELETE', '/api/periode-annees/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/mois', {
      statusCode: 200,
      body: [mois],
    });
  });

  afterEach(() => {
    if (periodeAnnee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/periode-annees/${periodeAnnee.id}`,
      }).then(() => {
        periodeAnnee = undefined;
      });
    }
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

  it('PeriodeAnnees menu should load PeriodeAnnees page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('periode-annee');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PeriodeAnnee').should('exist');
    cy.url().should('match', periodeAnneePageUrlPattern);
  });

  describe('PeriodeAnnee page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(periodeAnneePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PeriodeAnnee page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/periode-annee/new$'));
        cy.getEntityCreateUpdateHeading('PeriodeAnnee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', periodeAnneePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/periode-annees',
          body: {
            ...periodeAnneeSample,
            debut: mois,
            fin: mois,
          },
        }).then(({ body }) => {
          periodeAnnee = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/periode-annees+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/periode-annees?page=0&size=20>; rel="last",<http://localhost/api/periode-annees?page=0&size=20>; rel="first"',
              },
              body: [periodeAnnee],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(periodeAnneePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PeriodeAnnee page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('periodeAnnee');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', periodeAnneePageUrlPattern);
      });

      it('edit button click should load edit PeriodeAnnee page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PeriodeAnnee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', periodeAnneePageUrlPattern);
      });

      it('last delete button click should delete instance of PeriodeAnnee', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('periodeAnnee').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', periodeAnneePageUrlPattern);

        periodeAnnee = undefined;
      });
    });
  });

  describe('new PeriodeAnnee page', () => {
    beforeEach(() => {
      cy.visit(`${periodeAnneePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PeriodeAnnee');
    });

    it('should create an instance of PeriodeAnnee', () => {
      cy.get(`[data-cy="debut"]`).select(1);
      cy.get(`[data-cy="fin"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        periodeAnnee = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', periodeAnneePageUrlPattern);
    });
  });
});
