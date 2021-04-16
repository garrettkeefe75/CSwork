/*
 * Assignment 04 -- An exploration of polymorphism in multiple
 * inheritance.  This code is instrumented to allow the user to see
 * function calls as they happen.  
 * *
 * Note that while this code is motivated by a particular problem, 
 * this is just a solution sketch and much of the functionality is 
 * absent.  It is assumed that full functionality would exist, but
 * it is not needed for this example.  Students should not try to 
 * implement missing functionality.  Also note that most function
 * contracts are brief (because this is just a sketch).
 *
 * Additionally, while you can see multiple inheritance compile, its much
 * tougher to make it work properly.  Students should not fix the 
 * deficient design here.  Use this code to just see the order
 * that functions are called.  Later, you will describe possible 
 * fixes in essay questions.
 *
 * Peter Jensen
 * October 23, 2020
 */

#include <iostream>
#include <string>
#include <vector>

using namespace std;

class observer;  // Forward declare this class, we won't make/use it.

/*****************
 * class setting *
 *****************/

/* 
 * 'setting' objects represent some complicated data type 
 * with many getters/setters.  This would be used in a
 * large system (such as a multiplayer game or other
 * distributed application).  Since this is only an example, 
 * my setting class just holds one string containing any 
 * characters (a single setting).
 */
class setting
{
  private:
    string value;

  public:
    setting (string value);  
    virtual ~setting ();

    virtual void   change (string value);
    virtual string get ();
};


/* Constructor:  setting::setting
 *
 * Initializes the setting, sets it to some specific value
 * (given by the caller).  Assume that a data structure
 * is built here.
 */
setting::setting (string value)
  : value(value)
{
  cout << "      ==> setting::setting" << endl;
  /* Assume some data structure is updated here. */
  cout << "      <-- setting::setting" << endl;
}


/* Destructor:  setting::~setting
 *
 * Assume that some data structure within this object
 * is cleaned up here.
 */
setting::~setting ()
{
  cout << "      ==> setting::~setting" << endl;
  /* Assume some data structure is cleaned up here. */
  cout << "      <-- setting::~setting" << endl;
}


/* 
 * Change the setting to the specified value.
 */
void setting::change (string value)
{
  cout << "      ==> setting::change" << endl;
  this->value = value;
  cout << "      <-- setting::change" << endl;
}


/* 
 * Returns the value of the setting.
 */
string setting::get ()
{
  cout << "      ==> setting::get" << endl;
  cout << "      <-- setting::get" << endl;
  return value;
}


/************************
 * class server_setting *
 ************************/

/*
 * 'server_setting' objects represent a connection to some
 * remote data source (such as a server).  It inherits from
 * class 'setting'.  
 *
 * Whenever we use the get function to get the setting, this
 * class first checks to see if there is a newer setting 
 * value available remotely.  If so, it gets the remote
 * setting value and changes the value in this object.
 * Then, the current setting value is returned.  
 *
 * Whenever the set function is used to change a setting
 * value, it is also changed remotely (on the server).
 * (Both the 'get' and 'change' functions are overridden.)
 */
class server_setting : virtual public setting
{
  protected:
    bool   has_server_setting_changed();
    string get_server_setting();
    void   change_server_setting(string value);

  public:
    server_setting (string value);
    virtual ~server_setting ();

    virtual void   change (string value);
    virtual string get ();
};


/* Constructor:  server_setting::server_setting
 *
 * In addition to initializing the setting value,
 * a connection is established to some remote
 * storage of settings (such as a server or
 * database).
 */
server_setting::server_setting (string value)
  : setting (value)
{
  cout << "    ==> server_setting::server_setting" << endl;
  /* Assume the server_setting connection is set up here. */
  cout << "    <-- server_setting::server_setting" << endl;
}


/* Destructor:   server_setting::~server_setting
 *
 * Disconnects from some remote storage, cleans up
 * any allocated resources (memory, sockets, etc.)
 */
server_setting::~server_setting ()
{
  cout << "    ==> server_setting::~server_setting" << endl;
  /* Assume the server_setting connection is cleaned up here. */
  cout << "    <-- server_setting::~server_setting" << endl;
}


/* 
 * Updates the setting locally, and on any connected
 * remote setting storage.
 */
void server_setting::change (string value)
{
  cout << "    ==> server_setting::change" << endl;

  // Update the server_setting setting and also keep it (as a setting).

  this->change_server_setting (value);
  setting::change(value);              // Change superclass field

  cout << "    <-- server_setting::change" << endl;
}


/*
 * Returns the most up-to-date value for the setting.
 * If the remote value is newer, it is retrieved, kept,
 * and returned.  Otherwise, the current setting value
 * is returned.
 */
string server_setting::get ()
{
  cout << "    ==> server_setting::get" << endl;

  // If the remote setting has changed, we'll update this
  // object to the remote setting before returning it.

  // If the remote value has changed, retrieve it and
  // keep it.  (Note:  Do not call 'change' in this
  // class, otherwise this class will set the remote
  // value to the value we just got remotely.  Doh.)
  
  if (has_server_setting_changed())
    setting::change(this->get_server_setting());  // Superclass call

  // The setting in this object is now up-to-date.
  // Just get the setting (from the superclass) and return it.

  string result = setting::get();

  cout << "    <-- server_setting::get" << endl;

  return result;
}


/* 
 * Checks the remote server to see if there is a more
 * up-to-date setting.  Returns true if the server setting
 * is newer, false otherwise.
 */
bool server_setting::has_server_setting_changed()
{
  cout << "    ==> server_setting::has_server_setting_changed" << endl;
  
  /* Assume there would be code here to check the server_setting value. */
  
  cout << "    <-- server_setting::has_server_setting_changed" << endl;

  // This function might return true or false at any time (based
  // on some remote setting).  For this test, I'll just return
  // false.  (You MUST assume that true might be returned in
  // the actual, finished program.)

  bool debug_return_value = false;  // For testing, feel free to alter
  return debug_return_value;
}


/*
 * Retrieves the setting from the remote server and
 * returns it.
 */
string server_setting::get_server_setting()
{
  cout << "    ==> server_setting::get_server_setting" << endl; 
  /* Assume a server_setting setting is retrieved. */
  cout << "    <-- server_setting::get_server_setting" << endl;

  // Just a stub -- any setting might be returned.

  string debug_return_value = "anyvalue";  // Feel free to change.
  return debug_return_value;  
}


/*
 * Updates the setting on the remote server.
 */
void server_setting::change_server_setting(string value)
{
  cout << "    ==> server_setting::set_server_setting" << endl;  
  /* Assume the given setting is server_settingly stored/updated. */  
  cout << "    <-- server_setting::set_server_setting" << endl;
}


/****************
 * class notify *
 ****************/

/* The class 'notify' represents one possible implementation of 
 * the observer pattern.  The notify class inherits from the 
 * setting class.  
 *
 * 'Observer' objects can register themselves
 * with this object, and whenever the setting changes, all the 
 * observers will be notified.  Note that this notify class 
 * overrides the 'change' function (to allow notifications 
 * to take place when the setting changes).
 */
class notify : virtual public setting
{
  protected:
    void notify_observers ();

  public:
    notify (string initial_value);
    virtual ~notify ();

    virtual void change (string current_value);
    void register_observer (observer *);
};


/* Constructor: notify::notify
 * 
 * Initializes the setting with the given value
 * and sets up an empty list of observer objects.
 */
notify::notify (string initial_value)
  : setting(initial_value)
{
  cout << "    ==> notify::notify" << endl;
  /* Assume an observer list is set up. */
  cout << "    <-- notify::notify" << endl;
}

/* Destructor:  notify::~notify
 *
 * Cleans up the list of observers.
 */
notify::~notify ()
{
  cout << "    ==> notify::~notify" << endl;
  /* Assume an observer list is cleaned up. */
  cout << "    <-- notify::~notify" << endl;
}


/* 
 * If the new setting is different than
 * the old setting, the setting is changed and
 * the observer objects are all notified of the
 * change.
 */
void notify::change (string new_setting)
{
  cout << "    ==> notify::change" << endl;

  // Only change the setting and send out notifications 
  //   if the new setting is different than the old one.

  if (setting::get() != new_setting)
    {
      setting::change(new_setting);  // Change superclass field (avoids recursion)
      notify_observers ();
    }

  cout << "    <-- notify::change" << endl;
}


/*
 * All observers (that are 'listening' to this object)
 * are notified that the setting value has changed.
 */
void notify::notify_observers ()
{
  cout << "    ==> notify::notify_observers" << endl;
  /* Assume observer/delegates are activated. */
  cout << "    <-- notify::notify_observers" << endl;
}


/*
 * This function is provided so that external code can
 * register 'observer' objects.  These observer objects
 * will be called (notified) whenever this setting
 * value changes.
 */
void notify::register_observer (observer * o)
{
  cout << "    ==> notify::register_observer" << endl;
  /* Assume an observer is added to our list of observers. */
  cout << "    <-- notify::register_observer" << endl;
}


/***************
 * gui_setting *
 ***************/

/* 
 * 'gui_setting' objects represent a graphical setting
 * in a game.  
 *
 * A graphical setting may be changed locally, or 
 * updated from a remote server, so this class inherits 
 * from server_setting.
 *
 * Any changes to a graphical setting needs to be 
 * immediately drawn to the screen, so this class
 * inherits from notify (so that the objects responsible
 * for drawing the screen can be notified if the setting
 * changes).
 */
class gui_setting : virtual public notify, virtual public server_setting
{
public:
  gui_setting (string v);
  virtual ~gui_setting ();

  virtual void change (string v);
  virtual string get();
};

/* Constructor:  gui_setting::gui_setting
 *
 * Simply constructs the superclass portions of
 * this object.
 */
gui_setting::gui_setting (string v)
  : server_setting(v), notify(v), setting(v)
{
  cout << "  ==> gui_setting::gui_setting" << endl;
  cout << "  <-- gui_setting::gui_setting" << endl;
}

/* Destructor:  gui_setting::~gui_setting ()
 *
 * Nothing to do.  (Since the destructor is virtual,
 * the superclass destructors will be called on this
 * object after this destructor/function completes.
 */
gui_setting::~gui_setting ()
{
  cout << "  ==> gui_setting::~gui_setting" << endl;
  cout << "  <-- gui_setting::~gui_setting" << endl;
}

/*
 * Since we inherit a 'change' function from two
 * parent classes, the compiler requires us to
 * override it here (to prevent ambiguity).
 */
void gui_setting::change(string v)
{
  cout << "  ==> gui_setting::change" << endl;

  // We choose the call order.  I chose to call 'change'
  // in notify first, then server_setting second.

  notify::change(v);
  server_setting::change(v);

  cout << "  <-- gui_setting::change" << endl;
}

string gui_setting::get()
{
    cout << "  ==> gui_setting::get" << endl;
    string oldValue = setting::get();
    string newValue = server_setting::get();
    if(oldValue != newValue)
        notify::notify_observers();
    cout << "  <-- gui_setting::get" << endl;
    return newValue;
}


/********************
 * My main (tester) *
 ********************/

int main ()
{
  // Make one of our objects.

  cout << "Creating a gui_setting object:" << endl;
  cout << "------------------------------" << endl;

  gui_setting * g_setting = new gui_setting("low_resolution");

  cout << endl;
  
  // Add our setting' pointer to a few vectors.  This
  // will demonstrate polymorphism.  (The same object's
  // pointer will be added to all four vectors.)

  vector<gui_setting*>     vector_g;
  vector<server_setting*>  vector_r;
  vector<notify*>          vector_n;
  vector<setting*>         vector_s;

  vector_g.push_back(g_setting);
  vector_r.push_back(g_setting);
  vector_n.push_back(g_setting);
  vector_s.push_back(g_setting);

  // Use the pointer from each vector.  Set a setting,
  //   then get the setting.  Because of polymorphism,
  //   this will normally result in the exact same sequence
  //   of function calls.

  string result;

  // First, use a gui_setting pointer.

  cout << "Setting the user setting to \"foggy\" (through a gui_setting *):" << endl;
  cout << "----------------------------------------------------------------" << endl;

  gui_setting *t = vector_g[0];
  t->change("foggy");

  cout << endl;

  result = t->get();

  cout << endl;
  cout << "gui setting is now " << result << "." << endl; 
  cout << endl;

  // Next, use a server_setting pointer

  cout << "Setting the user setting to \"clear\" (through a server_setting *):" << endl;
  cout << "-------------------------------------------------------------------" << endl;

  server_setting *r = vector_r[0];
  r->change("clear");

  cout << endl;

  result = r->get();

  cout << endl;
  cout << "gui setting is now " << result << "." << endl; 
  cout << endl;

  // Next, use an notify pointer

  cout << "Setting the user setting to \"darkened\" (through an notify *):" << endl;
  cout << "---------------------------------------------------------------" << endl;

  notify* o = vector_n[0];
  o->change("darkened");

  cout << endl;

  result = o->get();

  cout << endl;
  cout << "gui setting is now " << result << "." << endl; 
  cout << endl;

  // Finally, use a setting pointer

  cout << "Setting the user setting to \"fancy\" (through a setting *):" << endl;
  cout << "------------------------------------------------------------" << endl;

  setting* s = vector_s[0];
  s->change("fancy");

  cout << endl;

  result = s->get();

  cout << endl;
  cout << "gui setting is now " << result << "." << endl; 
  cout << endl;

  // Done.  Delete our object (once only).  For fun, delete it
  //   using one of the base class pointers.  (We should still
  //   see all the destructors called.)
 
  cout << "Cleaning up:" << endl;
  cout << "------------" << endl;

  delete s;

  cout << endl;

  return 0;
}
