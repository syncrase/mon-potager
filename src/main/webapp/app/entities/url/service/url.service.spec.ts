import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUrl, Url } from '../url.model';

import { UrlService } from './url.service';

describe('Url Service', () => {
  let service: UrlService;
  let httpMock: HttpTestingController;
  let elemDefault: IUrl;
  let expectedResult: IUrl | IUrl[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UrlService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      url: 'AAAAAAA',
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

    it('should create a Url', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Url()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Url', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Url', () => {
      const patchObject = Object.assign(
        {
          url: 'BBBBBB',
        },
        new Url()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Url', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          url: 'BBBBBB',
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

    it('should delete a Url', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUrlToCollectionIfMissing', () => {
      it('should add a Url to an empty array', () => {
        const url: IUrl = { id: 123 };
        expectedResult = service.addUrlToCollectionIfMissing([], url);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(url);
      });

      it('should not add a Url to an array that contains it', () => {
        const url: IUrl = { id: 123 };
        const urlCollection: IUrl[] = [
          {
            ...url,
          },
          { id: 456 },
        ];
        expectedResult = service.addUrlToCollectionIfMissing(urlCollection, url);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Url to an array that doesn't contain it", () => {
        const url: IUrl = { id: 123 };
        const urlCollection: IUrl[] = [{ id: 456 }];
        expectedResult = service.addUrlToCollectionIfMissing(urlCollection, url);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(url);
      });

      it('should add only unique Url to an array', () => {
        const urlArray: IUrl[] = [{ id: 123 }, { id: 456 }, { id: 45261 }];
        const urlCollection: IUrl[] = [{ id: 123 }];
        expectedResult = service.addUrlToCollectionIfMissing(urlCollection, ...urlArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const url: IUrl = { id: 123 };
        const url2: IUrl = { id: 456 };
        expectedResult = service.addUrlToCollectionIfMissing([], url, url2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(url);
        expect(expectedResult).toContain(url2);
      });

      it('should accept null and undefined values', () => {
        const url: IUrl = { id: 123 };
        expectedResult = service.addUrlToCollectionIfMissing([], null, url, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(url);
      });

      it('should return initial array if no Url is added', () => {
        const urlCollection: IUrl[] = [{ id: 123 }];
        expectedResult = service.addUrlToCollectionIfMissing(urlCollection, undefined, null);
        expect(expectedResult).toEqual(urlCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
