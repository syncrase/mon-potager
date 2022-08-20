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

describe('BenthamHooker e2e test', () => {
  const benthamHookerPageUrl = '/bentham-hooker';
  const benthamHookerPageUrlPattern = new RegExp('/bentham-hooker(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const benthamHookerSample = {};

  let benthamHooker: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bentham-hookers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bentham-hookers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bentham-hookers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (benthamHooker) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bentham-hookers/${benthamHooker.id}`,
      }).then(() => {
        benthamHooker = undefined;
      });
    }
  });

  it('BenthamHookers menu should load BenthamHookers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bentham-hooker');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('BenthamHooker').should('exist');
    cy.url().should('match', benthamHookerPageUrlPattern);
  });

  describe('BenthamHooker page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(benthamHookerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create BenthamHooker page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bentham-hooker/new$'));
        cy.getEntityCreateUpdateHeading('BenthamHooker');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', benthamHookerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bentham-hookers',
          body: benthamHookerSample,
        }).then(({ body }) => {
          benthamHooker = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bentham-hookers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/bentham-hookers?page=0&size=20>; rel="last",<http://localhost/api/bentham-hookers?page=0&size=20>; rel="first"',
              },
              body: [benthamHooker],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(benthamHookerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details BenthamHooker page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('benthamHooker');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', benthamHookerPageUrlPattern);
      });

      it('edit button click should load edit BenthamHooker page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('BenthamHooker');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', benthamHookerPageUrlPattern);
      });

      it('last delete button click should delete instance of BenthamHooker', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('benthamHooker').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', benthamHookerPageUrlPattern);

        benthamHooker = undefined;
      });
    });
  });

  describe('new BenthamHooker page', () => {
    beforeEach(() => {
      cy.visit(`${benthamHookerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('BenthamHooker');
    });

    it('should create an instance of BenthamHooker', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        benthamHooker = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', benthamHookerPageUrlPattern);
    });
  });
});
