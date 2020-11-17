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

### Back up data
Possibly using firebase as a backend?

### Export Cycles
What's the purpose behind this one?

### Mark days that had a notation
What does this mean?

### Select Date from Calendar
Tap on the current day to get a calendar widget to switch days

### Sub-sensations

### Chart including Notes
New row in chart
* If the first character in the notes is an emoji, display emoji
* If the first character is a letter, display (i)

In both cases, display the full notes text on tap


### Filter Which Attributes are Displayed in Chart


### View Average Length of Cycle

### Graph Points as Color of Stamps (optional)

### Sex in Chart (with icons)
