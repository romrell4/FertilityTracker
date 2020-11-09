# FertilityTracker Roadmap

## V1

### [X] Enter Daily Symptoms
On the main page
Fields in the Form:
* Date (editable defaulting to today)
* Sensations (Radio - Dry, Smooth, Lubricative)
  * if Dry (Radio - Dry, Damp, Wet, Shiny)
  * if Smooth (Radio - Dry, Damp, Wet, Shiny)
  * if Lubricative (Radio - Damp, Shiny, Wet) – also check mucus
* Mucus (Checkbox)
  * If yes, Consistency: (Radio - NA, Sticky (1/4 inch or less), Tacky (1/2-3/4 inches), Stretchy (1 inch or more), Pasty, Gummy Gluey)
  * If yes, Color: (NA, Cloudy, Clear (even a little), Red/Pink, Brown, Yellow)
  * If yes, # Times: (Stepper: 1+)
* Bleeding (Checkbox)
  * If yes, Flow: (Radio - Heavy, Moderate, Light, Spotting)
* Sex (Checkbox)
  * If yes, Protected (Radio - Y/N)
* Notes (Free form - single line)

### [X] Viewing Live Symptom Chart
Allows you to slide to previous days
Scroll to the end by default

Rows:
* Day of Cycle
  * Date
  * Stamps (half and half, if applicable)
    * Bleeding/Spotting (red) – trump non-mucus
    * Non-peak mucus (pink)
    * Peak mucus (purple)
    * Non-mucus (green)
  * Sensation (D/S/L)

Stamp Tapping:
* If Bleeding -> (H/M/L/S)
* If Mucus -> Consistency/Color/# Times


Viewing Days of Interest
* First day of cycle
  * Definition: The first day of non-spotting bleeding
  * UI: Separator column to differentiate cycles
* Peak Mucus
  * Definition: Any day that has Clear or Stretchy mucus, or Lubricative sensation.


