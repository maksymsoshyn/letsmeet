# Let's Meet Simple App

It's simple utility application that allows you, using 2 different calendars, get a gap in which you can insert a new meeting
# Prerequirements
1. Maven
2. Java SE DK 8+
# How to install and use:
1. Go to your local projects repo directory

2. Install project using 
```bash
mvn clean install
```

3. Create 2 JSONs or 2 JS Objects


4. Run app using following command template

```bash
mvn exec:java -Dexec.mainClass="utility.lets_meet.Main" -Dexec.args="/path/to/dailyplan1.json /path/to/dailyplan2.json 00:30" -e
```
Note, that you can use raw strings instead of file path arguments. But remember about formatting. Examples are inside "examples" directory

# Structure of your meeting file or string

It can be JSON:
```textmate
{
  "working_hours": {
    "start": "09:00",
    "end": "20:00"
  },
  "planned_meeting": [
    {
      "start": "09:00",
      "end": "10:30"
    },
    {
      "start": "12:00",
      "end": "13:00"
    },
    {
      "start": "16:00",
      "end": "18:30"
    }
  ]
}
```

Or in JS Object format

```
{
  working_hours: {
    start: "09:00",
    end: "20:00"
  },
  planned_meeting: [
    {
      start: "09:00",
      end: "10:30"
    },
    {
      start: "12:00",
      end: "13:00"
    },
    {
      start: "16:00",
      end: "18:30"
    }
  ]
}
```

# Example files
You can find formatted file in "examples directory" 
