import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INomVernaculaire, NomVernaculaire } from '../nom-vernaculaire.model';

import { NomVernaculaireService } from './nom-vernaculaire.service';

describe('NomVernaculaire Service', () => {
  let service: NomVernaculaireService;
  let httpMock: HttpTestingController;
  let elemDefault: INomVernaculaire;
  let expectedResult: INomVernaculaire | INomVernaculaire[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NomVernaculaireService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nom: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a NomVernaculaire', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new NomVernaculaire()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NomVernaculaire', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a NomVernaculaire', () => {
      const patchObject = Object.assign(
        {
          nom: 'BBBBBB',
        },
        new NomVernaculaire()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NomVernaculaire', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nom: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a NomVernaculaire', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNomVernaculaireToCollectionIfMissing', () => {
      it('should add a NomVernaculaire to an empty array', () => {
        const nomVernaculaire: INomVernaculaire = { id: 123 };
        expectedResult = service.addNomVernaculaireToCollectionIfMissing([], nomVernaculaire);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nomVernaculaire);
      });

      it('should not add a NomVernaculaire to an array that contains it', () => {
        const nomVernaculaire: INomVernaculaire = { id: 123 };
        const nomVernaculaireCollection: INomVernaculaire[] = [
          {
            ...nomVernaculaire,
          },
          { id: 456 },
        ];
        expectedResult = service.addNomVernaculaireToCollectionIfMissing(nomVernaculaireCollection, nomVernaculaire);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NomVernaculaire to an array that doesn't contain it", () => {
        const nomVernaculaire: INomVernaculaire = { id: 123 };
        const nomVernaculaireCollection: INomVernaculaire[] = [{ id: 456 }];
        expectedResult = service.addNomVernaculaireToCollectionIfMissing(nomVernaculaireCollection, nomVernaculaire);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nomVernaculaire);
      });

      it('should add only unique NomVernaculaire to an array', () => {
        const nomVernaculaireArray: INomVernaculaire[] = [{ id: 123 }, { id: 456 }, { id: 13900 }];
        const nomVernaculaireCollection: INomVernaculaire[] = [{ id: 123 }];
        expectedResult = service.addNomVernaculaireToCollectionIfMissing(nomVernaculaireCollection, ...nomVernaculaireArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const nomVernaculaire: INomVernaculaire = { id: 123 };
        const nomVernaculaire2: INomVernaculaire = { id: 456 };
        expectedResult = service.addNomVernaculaireToCollectionIfMissing([], nomVernaculaire, nomVernaculaire2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nomVernaculaire);
        expect(expectedResult).toContain(nomVernaculaire2);
      });

      it('should accept null and undefined values', () => {
        const nomVernaculaire: INomVernaculaire = { id: 123 };
        expectedResult = service.addNomVernaculaireToCollectionIfMissing([], null, nomVernaculaire, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nomVernaculaire);
      });

      it('should return initial array if no NomVernaculaire is added', () => {
        const nomVernaculaireCollection: INomVernaculaire[] = [{ id: 123 }];
        expectedResult = service.addNomVernaculaireToCollectionIfMissing(nomVernaculaireCollection, undefined, null);
        expect(expectedResult).toEqual(nomVernaculaireCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
