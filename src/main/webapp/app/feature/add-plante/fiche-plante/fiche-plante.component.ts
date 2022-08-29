import {Component, OnInit} from '@angular/core';
import {SessionContextService} from "../../../shared/session-context/session-context.service";
import {ICronquistRank} from "../../../entities/cronquist-rank/cronquist-rank.model";
import {ScrapedPlante} from "../scraped-plant.model";
import {Reference} from "../../../entities/reference/reference.model";
import {ReferenceType} from "../../../entities/enumerations/reference-type.model";
import {Plante} from "../../../entities/plante/plante.model";

@Component({
  selector: 'jhi-fiche-plante',
  templateUrl: './fiche-plante.component.html',
  styleUrls: ['./fiche-plante.component.scss']
})
export class FichePlanteComponent implements OnInit {
  plante: Plante | null | undefined;
  cronquistClassification: ICronquistRank[] = [];
  images: Reference[] = [];
  sources: Reference[] = [];

  constructor(
    protected sessionContext: SessionContextService
  ) {
  }

  ngOnInit(): void {
    const scrapedPlante = this.sessionContext.get('plante') as ScrapedPlante | undefined;
    this.plante = scrapedPlante?.plante;
    if (scrapedPlante) {
      this.initClassifications(scrapedPlante);
      this.initReferences(scrapedPlante);
    }
// Dans l'objet reçu je n'ai pas les urls, la classification
//     let currentRank: ICronquistRank | null | undefined = this.scrapedPlante?.plante?.classification?.cronquist;
//     while (currentRank != null) {
//       this.cronquistClassification.push(currentRank);
//       currentRank = currentRank.parent;
//     }
  }

  previousState(): void {
    window.history.back();
  }

  private initReferences(scrapedPlante: ScrapedPlante): void {
    if (scrapedPlante.plante?.references) {
      for (const reference of scrapedPlante.plante.references) {
        if (reference.type === ReferenceType.SOURCE) {
          this.sources.push(reference);
        }
        if (reference.type === ReferenceType.IMAGE) {
          this.images.push(reference);
        }
      }
    }
  }

  private initClassifications(scrapedPlante: ScrapedPlante): void {
    const cronquistClassification: ICronquistRank[] = [];
    if (scrapedPlante.cronquistClassificationBranch) {
      for (const cronquist of scrapedPlante.cronquistClassificationBranch) {
        if (cronquist.nom) {
          cronquistClassification.push(cronquist);
        }
      }
      // Reverse in order to have the highest rank first
      this.cronquistClassification = [];
      for (let i = cronquistClassification.length - 1; i >= 0; i--) {
        this.cronquistClassification.push(cronquistClassification[i]);
      }
    }
  }

}
