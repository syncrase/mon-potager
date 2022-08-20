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

describe('APG e2e test', () => {
  const aPGPageUrl = '/apg';
  const aPGPageUrlPattern = new RegExp('/apg(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const aPGSample = {};

  let aPG: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/apgs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/apgs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/apgs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (aPG) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/apgs/${aPG.id}`,
      }).then(() => {
        aPG = undefined;
      });
    }
  });

  it('APGS menu should load APGS page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('apg');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('APG').should('exist');
    cy.url().should('match', aPGPageUrlPattern);
  });

  describe('APG page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(aPGPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create APG page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/apg/new$'));
        cy.getEntityCreateUpdateHeading('APG');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', aPGPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/apgs',
          body: aPGSample,
        }).then(({ body }) => {
          aPG = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/apgs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/apgs?page=0&size=20>; rel="last",<http://localhost/api/apgs?page=0&size=20>; rel="first"',
              },
              body: [aPG],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(aPGPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details APG page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('aPG');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', aPGPageUrlPattern);
      });

      it('edit button click should load edit APG page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('APG');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', aPGPageUrlPattern);
      });

      it('last delete button click should delete instance of APG', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('aPG').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', aPGPageUrlPattern);

        aPG = undefined;
      });
    });
  });

  describe('new APG page', () => {
    beforeEach(() => {
      cy.visit(`${aPGPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('APG');
    });

    it('should create an instance of APG', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        aPG = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', aPGPageUrlPattern);
    });
  });
});
