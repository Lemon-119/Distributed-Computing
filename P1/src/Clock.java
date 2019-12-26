//Name: Yifei Du
public class Clock implements Runnable{
	private String name;
	private long system_start_time = 0;
	private Monitor mo = null;
	private int totalMovie = 0;
	private int countMovie = 1;

	public Clock(String name, long time, Monitor mo, int totalMovie) {
	      this.name = name;
	      this.system_start_time = time;
	      this.mo = mo;
	      this.totalMovie = totalMovie;
	      new Thread(this).start();
	}
	
	public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] " + name + ": " + m);
    }
	
	public void run() {
		while(countMovie <= totalMovie) {
			try {
				Thread.sleep(3000);
				mo.movieStart();
				msg("--------------------------------------------------");
				msg("The " + countMovie +"th movie is begining.");
				Thread.sleep(3000);
				mo.movieEnd();
				msg("The " + countMovie +"th movie is over.");
				countMovie++;
				Thread.sleep(3000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		msg("Today all movies are over. Theatre is Closed Now!-------------------------------");
	}

}