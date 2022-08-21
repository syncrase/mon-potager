import {Directive, ElementRef, HostListener, Input, Renderer2} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {IReference} from "../../../../entities/reference/reference.model";

@Directive({
  selector: '[jhiBindReferenceTo]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: ReferenceDirective,
      multi: true
    }
  ]
})
export class ReferenceDirective implements ControlValueAccessor {
  @HostListener("input", ["$event.target.value"])
  onInput!: (value: any) => void;

  @Input() jhiBindReferenceTo!: keyof IReference;

  private reference: IReference | undefined;
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
      if (this.jhiBindReferenceTo === 'url') {
        this.reference!.url!.url = value;
      }
      if (this.jhiBindReferenceTo === 'description') {
        this.reference!.description = value;
      }
      fn(this.reference);
    };
  }

  registerOnTouched(fn: any): void {
    this._onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this._renderer.setProperty(this._elementRef.nativeElement, 'disabled', isDisabled);
  }

  writeValue(reference: any): void {
    // TODO bug: processed twice when adding or deleting a row
    if (!this.reference) {
      this.reference = (reference as IReference);
    } else {
      if (this.jhiBindReferenceTo === 'url') {
        this.reference.url!.url = reference.url.url;
      }
      if (this.jhiBindReferenceTo === 'description') {
        this.reference.description = reference.description;
      }
    }
    if (this.jhiBindReferenceTo === 'url' && this.reference.url?.url != null) {
      this._renderer.setAttribute(
        this._elementRef.nativeElement,
        "value",
        this.reference.url.url
      );
    }
    if (this.jhiBindReferenceTo === 'description' && this.reference.description != null) {
      this._renderer.setAttribute(
        this._elementRef.nativeElement,
        "value",
        this.reference.description
      );
    }
  }

}
