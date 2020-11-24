# FertilityTracker Roadmap

## V1

### Enter Daily Symptoms
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

### Viewing Live Symptom Chart
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

## V2
### Enter Daily Basal Body Temperature
Fields in the Form:
* Temperature (Decimal)
* Abnormal (Checkbox)
  * If yes, Notes (free form)
* Notes (String)

### Graph for Basal Body Temp
* Abnormal days with special icon
* Line graph (connected)

### Point Tap on Graph
* If normal day -> display temp
* If abnormal -> temp/notes

### View Previous Cycles
Select symptom/thermal/both to show up

### Graph Coverline
Definition
* Ovulation: First time in a cycle when three consecutive NORMAL temps are greater than the previous six NORMAL temps.
* Coverline: Highest of the six NORMAL low temps (see above) + 0.1 degrees

### Peak Day + 1/2/3
* Peak Day
  * Definition: Any occurrence in a cycle of the following: The last day of peak mucus, or the fourth consecutive day of non-peak mucus
  * UI: Background color (or alternative)
  * After peak day or day of spotting, the next three days also appear differently

## V3

### "When In Doubt"
A checkbox near the notes section that allows you to mark a day as "In Doubt" when it comes to fertility.
An info box can explain that "In Doubt" means that you would like to manually enforce the count-of-three rule due to unexpected temperatures and/or mucus.

Needs to apply to the count-of-three rule (showing up in the chart)

### Select Date from Calendar
[x] Tap on the current day to get a calendar widget to switch days
[x] Select Today

### Total days in cycle (only after cycle is complete)
[x] Display next to the dates?

### Sex in Chart (with icons)
Add to bottom of chart

## V4

### Back up data
Already done using shared preferences
[ ] Just need to put the app on the play store to make this work

### Export Cycles
What's the purpose behind this one?

## V5

### For Me tab
* Average Length of Cycle
* TODO

### Filter Which Attributes are Displayed in Chart

### Sub-sensations
