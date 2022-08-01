import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {IScrapedPlante} from "../scraped-plant.model";

export type EntityResponseType = HttpResponse<IScrapedPlante>;
export type EntityArrayResponseType = HttpResponse<IScrapedPlante[]>;

@Injectable({providedIn: 'root'})
export class AddPlanteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plantes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
  }

  search(plante: string): Observable<EntityResponseType> {
    return this.http.get<IScrapedPlante>(`${this.resourceUrl}/scrap`, {observe: 'response', params: {name: plante}});
  }

}
