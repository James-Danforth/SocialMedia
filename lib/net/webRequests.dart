import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';
//import '../models/PostMessage.dart';
//import '../main.dart';

// file currently seems irrelevant

Future<void> doRequests() async {
  var url = Uri.https('example.com', 'whatsit/create');             // using https; http also possible
  var response = await http.post(                                   // POST returning Future<Response>
                  url, 
                  body: {'name': 'doodle', 'color': 'blue'},
                  headers: {});
  print('Response status: ${response.statusCode}');
  print('Response body: ${response.body}');

  var url2 = Uri.http('www.cse.lehigh.edu', '~spear/courses.json');  // using http
  var response2 = await http.get(
                  url2, 
                  headers: {});                                      // GET returning Future<Response>
  print('Response2 status: ${response2.statusCode}');
  print('Response2 body: ${response2.body}');

  print(await http.read(Uri.https('example.com', 'foobar.txt')));    // GET returning Future<String>
}

Future<List<String>> getWebData() async {
  developer.log('Making web request...');
  // var url = Uri.http('www.cse.lehigh.edu', '~spear/courses.json');
  //var url = Uri.parse('http://www.cse.lehigh.edu/~spear/courses.json'); // list of strings
  var url = Uri.parse('http://www.cse.lehigh.edu/~spear/5k.json');
  // var url = Uri.parse('https://jsonplaceholder.typicode.com/albums/1');
  var headers = {"Accept": "application/json"};  // <String,String>{};

  var response = await http.get(url, headers: headers);  

  developer.log('Response status: ${response.statusCode}');
  //developer.log('Response headers: ${response.headers}');
  //developer.log('Response body: ${response.body}');

  final List<String> returnData;
  if (response.statusCode == 200) {
    // If the server did return a 200 OK response, then parse the JSON.
    var res = jsonDecode(response.body);
    print('json decode: $res');

    if( res is List ){
      returnData = (res as List<dynamic>).map( (x) => x.toString() ).toList();
    }else if( res is Map ){
      returnData = <String>[(res as Map<String,dynamic>).toString()];
    }else{
      developer.log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
  }else{
    throw Exception('Failed to retrieve web data (server returned ${response.statusCode})');
  }

  return returnData;
}

void my_async_post_method() async {
  var data = {'title': 'My first post'};
  var resp = await http.post(
    Uri.parse('https://jsonplaceholder.typicode.com/posts'),
    headers: {'Content-Type': 'application/json; charset=UTF-8'},
    body: json.encode(data),                                            // provided by `import 'dart:convert';`
  );
  print(resp.body);
}

// method for trying out a long-running calculation
Future<List<String>> doSomeLongRunningCalculation() async {
  developer.log("In dosomelongrunningcalculation");
  // return simpleLongRunningCalculation();  // we tried this, it worked
  return getWebData();
}