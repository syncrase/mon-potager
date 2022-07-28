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

describe('Temperature e2e test', () => {
  const temperaturePageUrl = '/temperature';
  const temperaturePageUrlPattern = new RegExp('/temperature(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const temperatureSample = {};

  let temperature: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/temperatures+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/temperatures').as('postEntityRequest');
    cy.intercept('DELETE', '/api/temperatures/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (temperature) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/temperatures/${temperature.id}`,
      }).then(() => {
        temperature = undefined;
      });
    }
  });

  it('Temperatures menu should load Temperatures page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('temperature');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Temperature').should('exist');
    cy.url().should('match', temperaturePageUrlPattern);
  });

  describe('Temperature page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(temperaturePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Temperature page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/temperature/new$'));
        cy.getEntityCreateUpdateHeading('Temperature');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', temperaturePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/temperatures',
          body: temperatureSample,
        }).then(({ body }) => {
          temperature = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/temperatures+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/temperatures?page=0&size=20>; rel="last",<http://localhost/api/temperatures?page=0&size=20>; rel="first"',
              },
              body: [temperature],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(temperaturePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Temperature page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('temperature');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', temperaturePageUrlPattern);
      });

      it('edit button click should load edit Temperature page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Temperature');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', temperaturePageUrlPattern);
      });

      it('last delete button click should delete instance of Temperature', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('temperature').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', temperaturePageUrlPattern);

        temperature = undefined;
      });
    });
  });

  describe('new Temperature page', () => {
    beforeEach(() => {
      cy.visit(`${temperaturePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Temperature');
    });

    it('should create an instance of Temperature', () => {
      cy.get(`[data-cy="min"]`).type('49035').should('have.value', '49035');

      cy.get(`[data-cy="max"]`).type('91290').should('have.value', '91290');

      cy.get(`[data-cy="description"]`).type('Ingenieur payment Ingenieur').should('have.value', 'Ingenieur payment Ingenieur');

      cy.get(`[data-cy="rusticite"]`).type('black Baby').should('have.value', 'black Baby');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        temperature = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', temperaturePageUrlPattern);
    });
  });
});
