import { OverviewSection } from "@/components/overview-section";
import { useUsers } from "@/context/context";


function Homepage() {
    const { hosts, habitations, reservations, loading } = useUsers();


    const totalReservationsBeds = habitations.length > 0 ? habitations.reduce((sum, h) => sum + h.rooms, 0) / habitations.length : 0;
    

    const totalHabitations = habitations.length;
    const totalHosts = hosts.length;
    const totalReservations = reservations.length;
    const mediaNumberRooms = Math.round(totalReservationsBeds * 10) / 10;



    if (loading) {
        return <div className="w-full h-screen flex items-center justify-center">Loading...</div>;
    }

    return (
        <div className="max-w-8xl h-screen flex flex-col items-center justify-start ">
            <div className="flex flex-col w-full p-4">
                <div className="container mx-auto px-4 space-y-8">
                    <section>
                        <h1 className="font-bold text-xl text-muted-foreground py-4">Overview</h1>
                        <OverviewSection 
                            totalHabitations={totalHabitations}
                            totalHosts={totalHosts}
                            totalReservations={totalReservations}
                            mediaNumberRooms={mediaNumberRooms}
                        />
                    </section>

                </div>
            </div>
        </div>
    )
}
export default Homepage