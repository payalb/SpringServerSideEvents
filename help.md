https://golb.hplar.ch/2017/03/Server-Sent-Events-with-Spring.html

<body>
<ul id="events"></ul>
<script type="application/javascript">
function add(message) {
    const el = document.createElement("li");
    el.innerHTML = message;
    document.getElementById("events").appendChild(el);
}

var eventSource = new EventSource("/temperature-stream");            // (1)
eventSource.onmessage = e => {                                       // (2)
    const t = JSON.parse(e.data);
    const fixed = Number(t.value).toFixed(2);
    add('Temperature: ' + fixed + ' C');
}
eventSource.onopen = e => add('Connection opened');                  // (3)
eventSource.onerror = e => add('Connection closed');                 //
</script>
</body>
Here, we are using the EventSource object pointed at /temperature-stream (1). This handles incoming messages by invoking the onmessage() function (2), error handling, and reaction to the stream opening, which are done in the same fashion (3). We should save this page as index.html and put it in the src/main/resources/static/ folder of our project. By default, Spring Web MVC serves the content of the folder through HTTP. Such behavior could be changed by providing a configuration that extends the WebMvcConfigurerAdapter class. 