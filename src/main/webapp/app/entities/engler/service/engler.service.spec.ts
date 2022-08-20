import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEngler, Engler } from '../engler.model';

import { EnglerService } from './engler.service';

describe('Engler Service', () => {
  let service: EnglerService;
  let httpMock: HttpTestingController;
  let elemDefault: IEngler;
  let expectedResult: IEngler | IEngler[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnglerService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Engler', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Engler()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Engler', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Engler', () => {
      const patchObject = Object.assign({}, new Engler());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Engler', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Engler', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEnglerToCollectionIfMissing', () => {
      it('should add a Engler to an empty array', () => {
        const engler: IEngler = { id: 123 };
        expectedResult = service.addEnglerToCollectionIfMissing([], engler);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engler);
      });

      it('should not add a Engler to an array that contains it', () => {
        const engler: IEngler = { id: 123 };
        const englerCollection: IEngler[] = [
          {
            ...engler,
          },
          { id: 456 },
        ];
        expectedResult = service.addEnglerToCollectionIfMissing(englerCollection, engler);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Engler to an array that doesn't contain it", () => {
        const engler: IEngler = { id: 123 };
        const englerCollection: IEngler[] = [{ id: 456 }];
        expectedResult = service.addEnglerToCollectionIfMissing(englerCollection, engler);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engler);
      });

      it('should add only unique Engler to an array', () => {
        const englerArray: IEngler[] = [{ id: 123 }, { id: 456 }, { id: 25077 }];
        const englerCollection: IEngler[] = [{ id: 123 }];
        expectedResult = service.addEnglerToCollectionIfMissing(englerCollection, ...englerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const engler: IEngler = { id: 123 };
        const engler2: IEngler = { id: 456 };
        expectedResult = service.addEnglerToCollectionIfMissing([], engler, engler2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engler);
        expect(expectedResult).toContain(engler2);
      });

      it('should accept null and undefined values', () => {
        const engler: IEngler = { id: 123 };
        expectedResult = service.addEnglerToCollectionIfMissing([], null, engler, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engler);
      });

      it('should return initial array if no Engler is added', () => {
        const englerCollection: IEngler[] = [{ id: 123 }];
        expectedResult = service.addEnglerToCollectionIfMissing(englerCollection, undefined, null);
        expect(expectedResult).toEqual(englerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
