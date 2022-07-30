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

describe('CronquistRank e2e test', () => {
  const cronquistRankPageUrl = '/cronquist-rank';
  const cronquistRankPageUrlPattern = new RegExp('/cronquist-rank(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const cronquistRankSample = { rank: 'INFRACLASSE' };

  let cronquistRank: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/cronquist-ranks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cronquist-ranks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cronquist-ranks/*').as('deleteEntityRequest');
  });

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

  it('CronquistRanks menu should load CronquistRanks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('cronquist-rank');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CronquistRank').should('exist');
    cy.url().should('match', cronquistRankPageUrlPattern);
  });

  describe('CronquistRank page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cronquistRankPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CronquistRank page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/cronquist-rank/new$'));
        cy.getEntityCreateUpdateHeading('CronquistRank');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cronquistRankPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cronquist-ranks',
          body: cronquistRankSample,
        }).then(({ body }) => {
          cronquistRank = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cronquist-ranks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cronquist-ranks?page=0&size=20>; rel="last",<http://localhost/api/cronquist-ranks?page=0&size=20>; rel="first"',
              },
              body: [cronquistRank],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(cronquistRankPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CronquistRank page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('cronquistRank');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cronquistRankPageUrlPattern);
      });

      it('edit button click should load edit CronquistRank page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CronquistRank');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cronquistRankPageUrlPattern);
      });

      it('last delete button click should delete instance of CronquistRank', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('cronquistRank').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cronquistRankPageUrlPattern);

        cronquistRank = undefined;
      });
    });
  });

  describe('new CronquistRank page', () => {
    beforeEach(() => {
      cy.visit(`${cronquistRankPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CronquistRank');
    });

    it('should create an instance of CronquistRank', () => {
      cy.get(`[data-cy="rank"]`).select('SOUSCLASSE');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        cronquistRank = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', cronquistRankPageUrlPattern);
    });
  });
});
