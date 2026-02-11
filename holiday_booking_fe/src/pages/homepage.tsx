import { OverviewSection } from "@/components/overview-section";
import { Statistics } from "@/components/statistics";
import { useUsers } from "@/context/context";


function Homepage() {
    const { hosts, habitations, resLastMonth, loading } = useUsers();


    const totalReservationsBeds = habitations.length > 0 ? habitations.reduce((sum, h) => sum + h.rooms, 0) / habitations.length : 0;


    const totalHabitations = habitations.length;
    const totalHosts = hosts.length;
    const reservationsLastMonth = resLastMonth.length;
    const mediaNumberRooms = Math.round(totalReservationsBeds * 10) / 10;



    if (loading) {
        return <div className="w-full h-screen flex items-center justify-center">Loading...</div>;
    }

    return (
        <div className="max-w-8xl h-screen flex flex-col items-center justify-start ">
            <div className="flex flex-col w-full p-4">
                <div className="container mx-auto px-4 space-y-8">
                    <section>
                        <h2 className="font-bold text-xl text-muted-foreground py-4">Overview</h2>
                        <OverviewSection
                            totalHabitations={totalHabitations}
                            totalHosts={totalHosts}
                            reservationsLastMonth={reservationsLastMonth}
                            mediaNumberRooms={mediaNumberRooms}
                        />
                    </section>

                    <section>
                        <h2 className="font-bold text-xl text-muted-foreground py-4">Featured</h2>
                        <Statistics/>
                    </section>
                    <section></section>
                    <section></section>
                </div>
            </div>
        </div>
    )
}
export default Homepage