import {Component, OnInit} from '@angular/core';
import {SessionContextService} from "../../../shared/session-context/session-context.service";
import {ICronquistRank} from "../../../entities/cronquist-rank/cronquist-rank.model";
import {ScrapedPlante} from "../scraped-plant.model";

@Component({
  selector: 'jhi-fiche-plante',
  templateUrl: './fiche-plante.component.html',
  styleUrls: ['./fiche-plante.component.scss']
})
export class FichePlanteComponent implements OnInit {
  scrapedPlante: ScrapedPlante | undefined;
  cronquistClassification: ICronquistRank[] | null | undefined;

  constructor(
    protected sessionContext: SessionContextService
  ) {
  }

  ngOnInit(): void {
    this.scrapedPlante = this.sessionContext.get('plante') as ScrapedPlante | undefined;
    this.cronquistClassification = this.scrapedPlante?.cronquistClassificationBranch;
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

}
