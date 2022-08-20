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

describe('Candolle e2e test', () => {
  const candollePageUrl = '/candolle';
  const candollePageUrlPattern = new RegExp('/candolle(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const candolleSample = {};

  let candolle: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/candolles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/candolles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/candolles/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (candolle) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/candolles/${candolle.id}`,
      }).then(() => {
        candolle = undefined;
      });
    }
  });

  it('Candolles menu should load Candolles page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('candolle');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Candolle').should('exist');
    cy.url().should('match', candollePageUrlPattern);
  });

  describe('Candolle page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(candollePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Candolle page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/candolle/new$'));
        cy.getEntityCreateUpdateHeading('Candolle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', candollePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/candolles',
          body: candolleSample,
        }).then(({ body }) => {
          candolle = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/candolles+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/candolles?page=0&size=20>; rel="last",<http://localhost/api/candolles?page=0&size=20>; rel="first"',
              },
              body: [candolle],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(candollePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Candolle page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('candolle');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', candollePageUrlPattern);
      });

      it('edit button click should load edit Candolle page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Candolle');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', candollePageUrlPattern);
      });

      it('last delete button click should delete instance of Candolle', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('candolle').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', candollePageUrlPattern);

        candolle = undefined;
      });
    });
  });

  describe('new Candolle page', () => {
    beforeEach(() => {
      cy.visit(`${candollePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Candolle');
    });

    it('should create an instance of Candolle', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        candolle = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', candollePageUrlPattern);
    });
  });
});
