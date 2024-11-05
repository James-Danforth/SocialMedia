import 'package:flutter/material.dart';
import 'package:the_buzz/models/PutLikes.dart';
import 'package:the_buzz/models/GetMessage.dart';
import 'package:the_buzz/models/PostMessage.dart';
import 'dart:developer' as developer;

//import './net/webRequests.dart'; // file is not used right now

void main() {
  runApp(const MyApp());
}

// MyApp class, which handles general stuff (color scheme, header name, project name)
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    const buzzScheme = ColorScheme.light(
      // bee themed color scheme
      primary: Colors.yellow,
      onPrimary: Colors.black,
    );

    return MaterialApp(
      title: 'Buzz App',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // TRY THIS: Try running your application with "flutter run". You'll see
        // the application has a purple toolbar. Then, without quitting the app,
        // try changing the seedColor in the colorScheme below to Colors.green
        // and then invoke "hot reload" (save your changes or press the "hot
        // reload" button in a Flutter-supported IDE, or press "r" if you used
        // the command line to start the app).
        //
        // Notice that the counter didn't reset back to zero; the application
        // state is not lost during the reload. To reset the state, use hot
        // restart instead.
        //
        // This works for code too, not just values: Most code changes can be
        // tested with just a hot reload.
        colorScheme: buzzScheme,
        useMaterial3: true,
      ),
      home: const MyHomePage(
        title: 'The Buzz   🐝',
      ),
    );
  }
}

// My home page, general class that defines variables and initialization state

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

// State of home page, handles instances, whenever code is run, a state is created
// which stores current instances. Creates the build, which is what the current page
// builds when state is initialized, general layout of page. Handles text controller,
// create post function (sets new state), and state initialization.

class _MyHomePageState extends State<MyHomePage> {
  late final TextEditingController
      _createTextController; // text grabber to get text from create mesage box
  late String newMessage = ""; // String that will hold new message to be posted

  @override
  void initState() {
    super.initState();
    _createTextController =
        TextEditingController(); // initializing as a controller for text field
  }

  void _createPost(String newMessage) async {
    // I should probably do this with a state class but a function probably works
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      if (newMessage != "") {
        // if message isn't null,convert to JSON so can be routed to backend
        developer.log("Creating post...");
        developer.log(newMessage);
        createNewMessage(newMessage);
      } else {
        developer.log("Entered null text"); // figure out better way to do this
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.

    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // TRY THIS: Try changing the color here to a specific color (to
        // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
        // change color while the other colors stay the same.
        backgroundColor: Theme.of(context)
            .colorScheme
            .primary, // Set background color to yellow. You can change this in the "MyApp" class.

        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
        actions: <Widget>[
          IconButton(
            icon: const Icon(Icons
                .person), //".person" denotes the icon. There are many icons such as ".add", ".settings", etc.
            onPressed: () {
              // Navigate to the user profile page when the button is pressed
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => UserProfilePage()),
              );
            },
          ),
        ],
      ),
      body: Column(children: [
        const Expanded(
          // expanded so no hass size error, listviewer has to occupy full space
          child: GetPost(), // list viewer
        ),
        Padding(
          padding: const EdgeInsets.all(8.0), // padding for aesthetics
          child: TextField(
            // input text field for post creation
            controller: _createTextController, // stating controller for text
            decoration: const InputDecoration(
              hintText: 'Post a message',
              border: OutlineInputBorder(),
            ),
            onSubmitted: (text) {
              newMessage =
                  text; // putting submitted text into late final string
            },
          ),
        ),
      ]),
      // plus button in bottom right, when pressed will create post with submitted text
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          developer.log("pressed");
          developer.log(newMessage);
          _createPost(newMessage);
        },
        tooltip: 'Create Post',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  @override // clearing the text after its been submitted
  void dispose() {
    _createTextController.dispose();
    super.dispose();
  }
}

// User profile page template, should display name, email, and note
class UserProfilePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('User Profile'),
      ),
      body: const Center(
        child: Text('User Profile Page Content'),
      ),
    );
  }
}

class Comments extends StatelessWidget {
  // This list of comments is in place of comments from the database for now.
  // Will be replaced when we connect to the backend.
  final List<Map<String, String>> comments = [
    {'username': 'user123', 'comment': 'Nice job :)!'},
    {'username': 'abc123', 'comment': 'Cool!'},
    {'username': 'xyz224', 'comment': 'Can you add more features?'},
  ];

  final TextEditingController _commentController = TextEditingController();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Comments'),
      ),
      // The comments from the route /comments/$id should be replaced when the route is up
      body: Column(
        children: <Widget>[
          Expanded(
            child: ListView.builder(
              itemCount: comments.length,
              itemBuilder: (BuildContext context, int index) {
                return ListTile(
                  title: Text(comments[index]['username']!),
                  subtitle: Text(comments[index]['comment']!),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(10.0), // padding for aesthetics
            child: Row(
              children: <Widget>[
                Expanded(
                  flex: 2,
                  child: TextField(
                    controller: _commentController,
                    decoration: const InputDecoration(
                      labelText: 'Comment',
                    ),
                  ),
                ),
                const SizedBox(width: 10.0),
                ElevatedButton(
                  onPressed: () {
                    _addComment();
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor:
                        Colors.black, // Change button color to blue
                  ),
                  child: const Text('Add Comment'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

// To add comments to a post, should be replaced by code to post comments to the backend
  void _addComment() {
    String comment = _commentController.text;
    if (comment.isNotEmpty) {
      comments.add({'username': 'abc000', 'comment': comment});
      _commentController.clear();
    }
  }
}

// GetPost class, class for listview, initializes state
class GetPost extends StatefulWidget {
  const GetPost({super.key});

  @override
  State<GetPost> createState() => _GetPostState();
}

// GetPost state class, initializes state, creates listview builder, and handles
// like updating function. When likes are added, sets state to reflect change.

class _GetPostState extends State<GetPost> {
  final _biggerFont = const TextStyle(fontSize: 18);
  final _smallerFont = const TextStyle(fontSize: 14);
  late Future<List<GetMessage>> _future_list_messages;

  @override
  void initState() {
    super.initState();
    _future_list_messages =
        fetchGetMessage(); // creates list of messages by calling fectchPostMessage()
  }

  void _retry() {
    setState(() {
      _future_list_messages = fetchGetMessage();
    });
  }

  @override
  Widget build(BuildContext context) {
    return postBuilder(context);
  }

  void _updateLikes(int newLikes, int id, bool isLiked) async {
    // add boolean to this, so if true add like, false remove
    setState(() {
      if (isLiked) {
        newLikes++;
        updateLikes(newLikes, id);
      } else {
        newLikes--;
        updateLikes(newLikes, id);
      }
    });
  }

  Widget postBuilder(BuildContext context) {
    var fb = FutureBuilder<List<GetMessage>>(
      future: _future_list_messages,
      builder:
          (BuildContext context, AsyncSnapshot<List<GetMessage>> snapshot) {
        Widget child;

        if (snapshot.hasData) {
          //developer.log('`using` ${snapshot.data}', name: 'my.app.category');
          // create  listview to show one row per array element of json response
          child = ListView.builder(
              //shrinkWrap: true, //expensive! consider refactoring. https://api.flutter.dev/flutter/widgets/ScrollView/shrinkWrap.html
              padding: const EdgeInsets.all(16.0),
              itemCount: snapshot.data!.length,
              itemBuilder: /*1*/ (context, i) {
                return Column(
                  children: <Widget>[
                    ListTile(
                      title: Text(
                        "${snapshot.data![i].message}",
                        style: _biggerFont,
                      ),
                      subtitle: Text(
                        "${snapshot.data![i].likes} Likes",
                        style: _smallerFont,
                      ),
                      leading: IconButton(
                        icon: Icon(Icons.hive),
                        // color: _isLiked ? Colors.black : Colors.yellow, // add later if need be
                        tooltip: "Adds like",
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(builder: (context) => Comments()),
                          );
                          setState(() {
                            snapshot.data![i].isLiked =
                                !(snapshot.data![i].isLiked);
                            _updateLikes(
                                snapshot.data![i].likes,
                                snapshot.data![i].id,
                                snapshot.data![i].isLiked);
                          });
                        },
                      ),
                      trailing: IconButton(
                          icon: Icon(Icons.thumbs_up_down),
                          onPressed: () {
                            print("We want to add a like!");
                          }),
                    ),
                    Divider(height: 1.0),
                  ],
                );
              });
        } else if (snapshot.hasError) {
          child = Text('${snapshot.error}');
        } else {
          // awaiting snapshot data, return simple text widget
          child =
              const CircularProgressIndicator(); // could show a loading spinner.
        }
        return child;
      },
    );

    return fb;
  }
}
