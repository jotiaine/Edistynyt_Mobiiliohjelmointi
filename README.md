# Android App Codebase Summary

This GitHub repository contains an Android application codebase that demonstrates various
functionalities and features. Here's a summary of the key components:

## Activities

MainActivity: Acts as the main entry point for the application. It sets up the navigation drawer,
toolbar, and navigation controller. It also handles menu creation and navigation actions.

## RecyclerView Adapters

ToDoAdapter: Manages the display of a list of to-do items in a RecyclerView. It implements the
ViewHolder pattern and provides item click listeners for navigation to a detail fragment.

![Get feedback](app/src/main/res/drawable/get_feedback_data.png)
![Post feedback](app/src/main/res/drawable/post_feedback.png)
![Detail feedback](app/src/main/res/drawable/feedback_detail.png)

## Custom Views

LatestDataView: A custom compound view that displays the latest data. It dynamically adds data rows
and animates their appearance using a fade-in animation.

CustomTemperatureView: A custom view for displaying the temperature. It performs custom drawing on
the canvas, allowing the temperature value to be updated and the view to be visually refreshed.

![Custom view tester 1](app/src/main/res/drawable/custom_view_tester1.png)
![Custom view tester 2](app/src/main/res/drawable/custom_view_tester2.png)

## Fragments

## Network-related Fragments

BasicAuthFragment: Makes a GET request to a specific URL using the Volley library for network
operations. It includes basic authentication and logs the response to the console.

![Basic auth](app/src/main/res/drawable/basic_auth.png)

FeedbackReadFragment: Retrieves feedback data from a server using the Volley library. It displays
the fetched feedback information on the UI.

![Directus feedbacks](app/src/main/res/drawable/directus_feedbacks.png)
![Directus read](app/src/main/res/drawable/directus_read.png)

FeedbackSendFragment: Handles sending feedback data to a server using the Volley library. It creates
a POST request, sets headers, and sends the data to the server.

![Directus send 1](app/src/main/res/drawable/directus_send1.png)
![Directus send 2](app/src/main/res/drawable/directus_send2.png)
![Directus send 3](app/src/main/res/drawable/directus_send3.png)
![Directus users](app/src/main/res/drawable/directus_users.png)

CityWeatherFragment: Displays weather information for a specific city. It retrieves weather data
from the OpenWeatherMap API using the Volley library.

## UI-related Fragments

CalendarFragment: Displays a calendar using a third-party library called CustomCalendarView. It
shows toast messages with selected date or month information.

ChartFragment: Displays a line chart using the AAChartModel and AAChartView libraries. It connects
to an MQTT broker, receives data payloads, and updates the chart series dynamically.
![Chart](app/src/main/res/drawable/chart.png)

CustomViewTesterFragment: Tests custom views, including a speedometer and a temperature gauge. It
connects to an MQTT broker, receives data payloads, and updates the UI elements based on the
received data.

MapsFragment: Displays a Google Map using the Google Maps API. It initializes the map, sets up
markers, and handles marker click events.
![Google maps](app/src/main/res/drawable/googlemaps_api.png)
![Google maps marker](app/src/main/res/drawable/googlemaps_marker.png)

OpenStreetMapFragment: Displays a map using the OpenStreetMap library (osmdroid). It initializes the
map, sets the tile source, zoom level, and adds a marker to a specific location.
![OpenStreetMap](app/src/main/res/drawable/openstreetmap.png)

RemoteMessageFragment: Connects to an MQTT broker using the HiveMQ client library. It handles the
MQTT connection and response.'
![HiveMQ](app/src/main/res/drawable/hive_mq.png)

FeedbackDetailFragment: Displays detailed information about a to-do item. It fetches data from a
JSON
API and displays it on the UI.

TempAccessFragment: Handles temporary access to a service using Volley for network requests. It
manages login, token handling, and data retrieval functionalities.
![Temp access](app/src/main/res/drawable/temp_access.png)

FeedbackDataFragment: Interacts with a remote service using Volley. It displays todo items in a
RecyclerView and provides buttons for getting and posting todo data.

WeatherStationFragment: Connects to an MQTT broker, receives MQTT messages containing weather data,
and updates the UI elements with the received information.
![Weather station](app/src/main/res/drawable/weather_station.png)

![Calendar](app/src/main/res/drawable/calendar.png)

![Home animation](app/src/main/res/drawable/home_anim.png)

These code files showcase a variety of functionalities, including network requests, data display,
custom views, calendar integration, map integration, and MQTT communication.