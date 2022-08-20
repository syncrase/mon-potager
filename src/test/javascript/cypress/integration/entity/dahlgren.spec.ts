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

describe('Dahlgren e2e test', () => {
  const dahlgrenPageUrl = '/dahlgren';
  const dahlgrenPageUrlPattern = new RegExp('/dahlgren(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const dahlgrenSample = {};

  let dahlgren: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/dahlgrens+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/dahlgrens').as('postEntityRequest');
    cy.intercept('DELETE', '/api/dahlgrens/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (dahlgren) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/dahlgrens/${dahlgren.id}`,
      }).then(() => {
        dahlgren = undefined;
      });
    }
  });

  it('Dahlgrens menu should load Dahlgrens page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dahlgren');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Dahlgren').should('exist');
    cy.url().should('match', dahlgrenPageUrlPattern);
  });

  describe('Dahlgren page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(dahlgrenPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Dahlgren page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/dahlgren/new$'));
        cy.getEntityCreateUpdateHeading('Dahlgren');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dahlgrenPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/dahlgrens',
          body: dahlgrenSample,
        }).then(({ body }) => {
          dahlgren = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/dahlgrens+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/dahlgrens?page=0&size=20>; rel="last",<http://localhost/api/dahlgrens?page=0&size=20>; rel="first"',
              },
              body: [dahlgren],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(dahlgrenPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Dahlgren page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('dahlgren');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dahlgrenPageUrlPattern);
      });

      it('edit button click should load edit Dahlgren page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Dahlgren');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dahlgrenPageUrlPattern);
      });

      it('last delete button click should delete instance of Dahlgren', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('dahlgren').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', dahlgrenPageUrlPattern);

        dahlgren = undefined;
      });
    });
  });

  describe('new Dahlgren page', () => {
    beforeEach(() => {
      cy.visit(`${dahlgrenPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Dahlgren');
    });

    it('should create an instance of Dahlgren', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        dahlgren = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', dahlgrenPageUrlPattern);
    });
  });
});
