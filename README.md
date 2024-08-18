# Smart Home Client - Group 8

## Overview
This project is a Smart Home Server and Client implementation that facilitates the management and control of various household smart devices. The system is designed with a structured architecture that dynamically generates the user interface (UI) on the client side, based on the devices available and their states. The client fetching the devices data from the server, the server creates a mocking devices - but the project designed in a way that every data or action on a devices is a REST URL path, GET for data and POST for actions, in order to easily deploy to a real life scenario where the edge devices are real devices.

<h2>Key Features</h2>

<ol>
    <li>
        <strong>Dynamic Device Management</strong>
        <p>
            The system provides robust management for a variety of household devices, each represented by a class in a well-structured hierarchy. From simple switches and sliders to more sophisticated components like live camera streams, the architecture allows for seamless addition, removal, and interaction with devices. The system's dynamic nature ensures that new devices can be integrated effortlessly, and existing devices can be managed efficiently, all within a unified framework.
        </p>
    </li>
    <li>
        <strong>Server-Driven API and UI Interaction</strong>
        <p>
            The server plays a critical role in handling device interactions and API endpoints, providing real-time updates to the client-side UI. By managing API endpoints dynamically based on the devices connected, the server ensures that the UI is always up-to-date with the latest device configurations and states. This approach eliminates the need for manual updates on the client side, as any changes made on the server are automatically reflected in the user interface, providing a consistent and responsive user experience.
        </p>
    </li>
    <li>
        <strong>Persistent State and User Preferences</strong>
        <p>
            The system is designed with a focus on user experience, offering persistent state management across sessions. User interactions, such as device configurations and selections, are saved by the server, allowing users to pick up right where they left off, even after logging out or disconnecting. This persistence extends to device settings, ensuring that the system remains tailored to individual preferences, enhancing usability and continuity for the end-user.
        </p>
    </li>
</ol>


<h2>Project Requirements</h2>

<ol>
    <li>
        <strong>Collections:</strong>
        <ul>
            <li><strong>ArrayList</strong> and <strong>List</strong>: Used extensively for managing collections of devices, actions, and device information. Examples include:
                <ul>
                    <li><code>List&lt;Info&gt;</code> in the <code>Device</code> class for storing device information.</li>
                    <li><code>List&lt;DeviceAction&gt;</code> in the <code>DeviceActionController</code> class for managing device actions.</li>
                    <li><code>List&lt;HouseholdDevice&gt;</code> in the <code>Mocking</code> class for creating and storing mock devices.</li>
                    <li><code>List&lt;DeviceInfo&gt;</code> in the <code>DeviceDataController</code> class for holding device data.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Abstract Classes:</strong>
        <ul>
            <li><strong>Device:</strong> The <code>Device</code> class serves as an abstract base class for all device types, defining common properties and methods that are inherited by subclasses like <code>HouseholdDevice</code>.</li>
        </ul>
    </li>
    <li>
        <strong>Classes:</strong>
        <ul>
            <li>A total of 22 unique classes are implemented, covering both server and client functionality. Key classes include <code>Main</code>, <code>HouseholdDevice</code>, <code>DeviceAction</code>, <code>AddDevicesPanel</code>, <code>DataActionPanel</code>, <code>ServerController</code>, and <code>SharedDB</code>.</li>
        </ul>
    </li>
    <li>
        <strong>Iterators:</strong>
        <ul>
            <li>Used in several places for looping through collections, such as:
                <ul>
                    <li>Iterating through the list of <code>DeviceInfo</code> in the <code>DeviceDataController</code> class.</li>
                    <li>Looping through <code>DeviceAction</code> objects in the <code>DeviceActionController</code> class.</li>
                    <li>Iterating through <code>HouseholdDevice</code> objects in the <code>AddDevicesPanel</code> and <code>MainPanel</code> classes.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Containers:</strong>
        <ul>
            <li><strong>Swing Containers:</strong> Used extensively in the client-side UI, such as:
                <ul>
                    <li><code>JPanel</code> in <code>AddDevicesPanel</code> and <code>DataActionPanel</code> for organizing UI components.</li>
                    <li><code>JScrollPane</code> in <code>AddDevicesPanel</code> to allow scrolling through the table of devices.</li>
                    <li><code>CardLayout</code> in the <code>MainPanel</code> class for switching between different views.</li>
                </ul>
            </li>
            <li><strong>Collections:</strong> <code>ArrayList</code> and <code>List</code> are used as data containers throughout the project.</li>
        </ul>
    </li>
    <li>
        <strong>Dynamic Functionality:</strong>
        <ul>
            <li>The project supports adding, removing, displaying, and sorting devices dynamically, as seen in:
                <ul>
                    <li>The <code>Main</code> class, which dynamically adds endpoints and starts the server.</li>
                    <li><code>AddDevicesPanel</code>, which allows users to add new devices, and sort them using comparators.</li>
                    <li><code>Mocking</code>, where mock devices are dynamically created and added to a list.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Inheritance:</strong>
        <ul>
            <li>Implemented with several classes, such as:
                <ul>
                    <li><code>Device</code>: An abstract class that serves as a base for <code>HouseholdDevice</code>.</li>
                    <li><code>Widget</code>: A base class for UI widgets like <code>Switch</code>, <code>Slider</code>, and <code>Dropdown</code>.</li>
                    <li><code>CameraStream</code>, <code>Dropdown</code>, and <code>Slider</code> all extend <code>Widget</code>.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Polymorphism:</strong>
        <ul>
            <li>Used in managing different types of devices and actions, particularly in:
                <ul>
                    <li><code>HouseholdDevice</code> objects being referenced through the <code>Device</code> superclass.</li>
                    <li><code>Widget</code> class serving as a base for different UI components like <code>Switch</code>, <code>Slider</code>, and <code>Dropdown</code>, allowing them to be used interchangeably.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>UI:</strong>
        <ul>
            <li>Extensive use of Swing components to build the client-side interface, such as:
                <ul>
                    <li><code>JPanel</code>, <code>JLabel</code>, <code>JButton</code>, <code>JTable</code>, and <code>JScrollPane</code> in <code>AddDevicesPanel</code>, <code>DataActionPanel</code>, and <code>MainPanel</code>.</li>
                    <li>Custom UI components like a gradient-colored slider in <code>GradientSliderUI</code>.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Error-Catching Mechanism:</strong>
        <ul>
            <li>Error handling is implemented in several classes using <code>try-catch</code> blocks, such as:
                <ul>
                    <li><code>ServerController</code> for handling HTTP request errors.</li>
                    <li><code>RestWrapper</code> for managing exceptions during HTTP communication between the client and server.</li>
                    <li><code>AddDevicesPanel</code> and <code>DataActionPanel</code> for handling exceptions during UI updates and device actions.</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>
        <strong>Comparators:</strong>
        <ul>
            <li>Used for sorting devices in:
                <ul>
                    <li><code>AddDevicesPanel</code>: Sorting devices by name or room.</li>
                    <li><code>MainPanel</code>: Sorting devices by name or room using a <code>Comparator&lt;HouseholdDevice&gt;</code>.</li>
                </ul>
            </li>
        </ul>
    </li>
</ol>

## Architectural Design
<img src="https://github.com/user-attachments/assets/27c11120-34d4-4fed-8ef0-769a8c52b0fd" alt="alt text" width="480" height="640">|

The system follows a modular design, with each component playing a specific role:

- **Device Class**: The base class for all devices, containing common attributes such as deviceName and description.
- **HouseholdDevice Class**: Extends the Device class, designed so we can represent every new smart home device in the future, every device have dataController for device realtime data and actionController for sending commands:
  - **Controllers**: The DeviceActionController and DeviceDataController manage the actions and data related to devices. They ensure that commands and data flows are handled correctly and are kept in sync with the server.
- **Info and DeviceInfo Classes**: Store information related to devices, which is used to generate the UI and manage device states.
- **DeviceAction and Widget Classes**: Handle specific actions and UI elements associated with devices. These classes ensure that each device can interact with the user interface seamlessly, and it is the key-feature for scalability in our project (easy support for new devices in the future).


## How to Use

1. **Setup**: Clone the client and server repositories, first run the server then the client as shown in the video below.
2. **Add Devices**: Add your household devices by opening a window with the server-connected devices through the button.
3. **Actions & Data**: After adding devices from the server enter the Actions & Data page, send data to the devices through the server, and see live data.

**Walkthrough Videos**:



https://github.com/user-attachments/assets/98e5a308-b46c-4a67-9109-ac508599a38f






https://github.com/user-attachments/assets/52f4eb5c-b1d1-44f4-a443-f944792480af


**UML Reference**




<img src="https://github.com/rranlevi/SmartHome_Server/blob/master/Client_UML.png" alt="alt text" width="620" height="320">

## Credits

This project was made possible by the contributions of the following individuals:

- **Ran Levi**
- **Harel Markel**
- **Alex Makarov**
- **Ron Yamin**

## Licensing

Copyright (c) 2024 Ran Levi, Harel Markel, Alex Makarov, Ron Yamin

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---
