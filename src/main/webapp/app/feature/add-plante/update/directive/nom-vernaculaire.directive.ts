import {Directive, ElementRef, HostListener, Input, Renderer2} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {INomVernaculaire} from "../../../../entities/nom-vernaculaire/nom-vernaculaire.model";

@Directive({
  selector: '[jhiBindNomVernaculaireTo]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: NomVernaculaireDirective,
      multi: true
    }
  ]
})
export class NomVernaculaireDirective implements ControlValueAccessor {
  @HostListener("input", ["$event.target.value"])
  onInput!: (value: any) => void;

  @Input() jhiBindNomVernaculaireTo!: keyof INomVernaculaire;

  private nomVernaculaire: INomVernaculaire | undefined;
  private _onTouched: any;

  /**
   * https://indepth.dev/tutorials/angular/object-in-formcontrol
   *
   */
  constructor(
    private _elementRef: ElementRef<HTMLInputElement>,
    private _renderer: Renderer2
  ) {
  }

  registerOnChange(fn: any): void {
    this.onInput = value => {
      this.nomVernaculaire![this.jhiBindNomVernaculaireTo] = value;
      fn(this.nomVernaculaire);
    };
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'disabled', isDisabled);
  }

  writeValue(nomVernaculaire: any): void {
    // TODO bug: processed twice when adding or deleting a row
    if (!this.nomVernaculaire) {
      this.nomVernaculaire = (nomVernaculaire as INomVernaculaire);
    } else {
      this.copyValue<keyof INomVernaculaire, INomVernaculaire>(nomVernaculaire, this.nomVernaculaire, this.jhiBindNomVernaculaireTo);
    }
    if (this.nomVernaculaire[this.jhiBindNomVernaculaireTo] != null) {
      const value = <string>this.nomVernaculaire[this.jhiBindNomVernaculaireTo];
      this._renderer.setAttribute(
        this._elementRef.nativeElement,
        "value",
        value
      );
    }
  }

  private copyValue = <U extends keyof T, T extends object>(from: T, to: T, key: U): void => {
    to[key] = from[key];
  };

}
